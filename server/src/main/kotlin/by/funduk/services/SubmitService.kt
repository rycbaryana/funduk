package by.funduk.services

import by.funduk.db.Submissions
import by.funduk.db.Tasks
import by.funduk.db.Users
import by.funduk.db.query
import by.funduk.model.*
import by.funduk.ui.SubmissionView
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll

object SubmitService {

    suspend fun submitAndTest(submission: Submission) {
        insert(submission)
        TestService.test(submission)
    }

    suspend fun insert(submission: Submission): Int = query {
//        val valid = Submissions.selectAll().where{(Submissions.taskId eq  submission.taskId) and (Submissions.userId eq submission.userId) and (Submissions.code eq submission.code)}.empty()
        Submissions.insert {
            it[taskId] = submission.taskId
            it[userId] = submission.userId
            it[submitTime] = submission.submitTime.toJavaLocalDateTime()
            it[code] = submission.code
            it[language] = submission.language.text
        }[Submissions.id].value
    }

    suspend fun getSubmission(id: Int): Submission? = query {
        Submissions.selectAll()
            .where { Submissions.id eq id }
            .map {
                Language.entries.find { entry -> entry.text == it[Submissions.language] }?.let { it1 ->
                    val verdict = it[Submissions.verdict]
                    val time = it[Submissions.timeElapsed]
                    val mem = it[Submissions.memoryUsed]

                    println("database $verdict $time $mem")

                    var info: TestInfo? = null
                    if (verdict != null && time != null && mem != null) {
                        info = Status.entries.find { entry -> entry.toString().startsWith(verdict) }?.let { it2 ->
                            TestInfo(
                                it2,
                                0,
                                time,
                                mem
                            )
                        }
                    }

                    Submission(
                        it[Submissions.id].value,
                        it[Submissions.taskId].value,
                        it[Submissions.userId].value,
                        it[Submissions.submitTime].toKotlinLocalDateTime(),
                        it[Submissions.code],
                        it1,
                        info
                    )
                }
            }
            .singleOrNull()
    }

    suspend fun getSubmissionView(id: Int): SubmissionView? = query {
        Submissions.selectAll()
            .where { Submissions.id eq id }
            .map {
                Language.entries.find { entry -> entry.text == it[Submissions.language] }?.let { it1 ->
                    val verdict = it[Submissions.verdict]
                    val time = it[Submissions.timeElapsed]
                    val mem = it[Submissions.memoryUsed]

                    var info: TestInfo? = null
                    if (verdict != null && time != null && mem != null) {
                        info = Status.entries.find { entry -> entry.toString().startsWith(verdict) }?.let { it2 ->
                            TestInfo(
                                it2,
                                0,
                                time,
                                mem
                            )
                        }
                    }

                    Tasks.selectAll()
                        .where { Tasks.id eq it[Submissions.taskId].value }
                        .map {
                            it[Tasks.name]
                        }
                        .singleOrNull()?.let { it2 ->
                            Users.selectAll().where { Users.id eq it[Submissions.userId].value }
                                .map {
                                    it[Users.username]
                                }
                                .singleOrNull()?.let { it3 ->
                                    SubmissionView(
                                        it[Submissions.id].value,
                                        it[Submissions.taskId].value,
                                        it[Submissions.userId].value,
                                        it2,
                                        it3,
                                        it[Submissions.submitTime].toKotlinLocalDateTime(),
                                        it1,
                                        info
                                    )
                                }
                        }
                }
            }
            .singleOrNull()
    }

    suspend fun getSubmissionViews(taskId: Int, userId: Int, count: Int, offset: Int): List<SubmissionView> = query {
        Submissions.selectAll().limit(count).offset(offset.toLong())
            .where { (Submissions.taskId eq taskId) and (Submissions.userId eq userId) }
            .map {
                Language.entries.find { entry -> entry.text == it[Submissions.language] }?.let { it1 ->
                    val verdict = it[Submissions.verdict]
                    val time = it[Submissions.timeElapsed]
                    val mem = it[Submissions.memoryUsed]

                    var info: TestInfo? = null
                    if (verdict != null && time != null && mem != null) {
                        info = Status.entries.find { entry -> entry.toString().startsWith(verdict) }?.let { it2 ->
                            TestInfo(
                                it2,
                                0,
                                time,
                                mem
                            )
                        }
                    }

                    Tasks.selectAll()
                        .where { Tasks.id eq it[Submissions.taskId].value }
                        .map {
                            it[Tasks.name]
                        }
                        .singleOrNull()?.let { it2 ->
                            Users.selectAll().where { Users.id eq it[Submissions.userId].value }
                                .map {
                                    it[Users.username]
                                }
                                .singleOrNull()?.let { it3 ->
                                    SubmissionView(
                                        it[Submissions.id].value,
                                        it[Submissions.taskId].value,
                                        it[Submissions.userId].value,
                                        it2,
                                        it3,
                                        it[Submissions.submitTime].toKotlinLocalDateTime(),
                                        it1,
                                        info
                                    )
                                }
                        }
                }
            }.filterNotNull()
    }

    suspend fun delete(id: Int): Boolean {
        return query {
            Submissions.deleteWhere { Submissions.id eq id }
        } > 0
    }
}