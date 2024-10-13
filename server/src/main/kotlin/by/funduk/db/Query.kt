package by.funduk.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> query (block: suspend () -> T) : T = newSuspendedTransaction(Dispatchers.IO) {
    block()
}