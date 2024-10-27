package by.funduk

import by.funduk.db.Users
import by.funduk.services.UserService
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserServiceTest {

    @Before
    fun setup() {
        Database.connect("jdbc:h2:mem:funduk_test;DB_CLOSE_DELAY=-1", user="root", password = "", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Users)
        }
    }

    @After
    fun teardown() {
        transaction {
            SchemaUtils.drop(Users)
        }
    }

    @Test
    fun testAddUser() = runBlocking {
        val user = UserService.addUser("testUser", "hashedPassword")
        assertNotNull(user)
        assertEquals("testUser", user.username)
    }

    @Test
    fun testFindByUsername() = runBlocking {
        UserService.addUser("testUser", "hashedPassword")

        val foundUser = UserService.findByUsername("testUser")
        assertNotNull(foundUser)
        assertEquals("testUser", foundUser.username)

        val notFoundUser = UserService.findByUsername("nonExistentUser")
        assertNull(notFoundUser)
    }
}