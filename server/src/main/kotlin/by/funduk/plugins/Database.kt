package by.funduk.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() = Database.connect(
    "jdbc:h2:mem:funduk;DB_CLOSE_DELAY=-1",
    user = "root",
    driver = "org.h2.Driver",
    password = ""
//        "jdbc:postgresql://localhost:5432/postgres",
//        user = "funduk",
//        password = "1234"
)
