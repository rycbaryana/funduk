package by.funduk.utils

import com.auth0.jwt.interfaces.Payload

import org.mindrot.jbcrypt.BCrypt

fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun checkPassword(password: String, hashed: String): Boolean {
    return BCrypt.checkpw(password, hashed)
}

fun extractUserId(payload: Payload): Int = payload.getClaim("user_id").asInt()