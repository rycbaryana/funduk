package by.funduk

import by.funduk.internal.db.Users
import by.funduk.internal.services.UserService
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserServiceTest : BaseServiceTest(Users) {
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