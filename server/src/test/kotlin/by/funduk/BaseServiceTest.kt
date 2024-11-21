package by.funduk

import by.funduk.internal.db.Tasks
import by.funduk.internal.db.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before

abstract class BaseServiceTest(private vararg val tables: Table) {
    @Before
    fun setup() {
        Database.connect("jdbc:h2:mem:funduk_test;DB_CLOSE_DELAY=-1", user="root", password = "", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(*tables)
        }
    }

    @After
    fun teardown() {
        transaction {
            SchemaUtils.drop(*tables)
        }
    }
}