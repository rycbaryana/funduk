package by.funduk.services

import by.funduk.AuthConfig
import by.funduk.db.RefreshTokens
import by.funduk.db.query
import by.funduk.model.User
import by.funduk.utils.extractUserId
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import java.util.*

object AuthService {

    val verifier: JWTVerifier =
        with(AuthConfig) { JWT.require(Algorithm.HMAC256(secret)).withAudience(audience).withIssuer(issuer).build() }

    fun createAccessToken(userId: Int): String = createToken(userId, 86_400_000)

    suspend fun createRefreshToken(userId: Int): String = createToken(userId, 86_400_000).also {
        registerRefreshToken(userId, it)
    }

    private suspend fun registerRefreshToken(userId: Int, token: String) = query {
        RefreshTokens.upsert(RefreshTokens.userId) {
            it[RefreshTokens.userId] = userId
            it[RefreshTokens.token] = token
        }
    }

    suspend fun validate(credential: JWTCredential): JWTPrincipal? {
        val userId = extractUserId(credential.payload)
        val user: User? = userId.let {
            UserService.findByUserId(it)
        }
        return user?.let {
            JWTPrincipal(credential.payload)
        }
    }

    suspend fun refreshToken(refreshToken: String): Pair<String, String>? {
        val decoded = verifier.verify(refreshToken)
        val userId = extractUserId(decoded)
        val refreshTokenOwner: Int? = getRefreshTokenOwner(refreshToken)
        refreshTokenOwner?.let {
            if (userId == refreshTokenOwner) {
                return createAccessToken(it) to createRefreshToken(it)
            }
        }
        return null
    }


    private suspend fun getRefreshTokenOwner(token: String): Int? = query {
        RefreshTokens.selectAll().where { RefreshTokens.token eq token }
            .map { it[RefreshTokens.userId] }.singleOrNull()?.value
    }

    private fun createToken(userId: Int, expireIn: Int): String =
        JWT.create()
            .withAudience(AuthConfig.audience)
            .withIssuer(AuthConfig.issuer)
            .withClaim("user_id", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + expireIn))
            .sign(Algorithm.HMAC256(AuthConfig.secret))
}