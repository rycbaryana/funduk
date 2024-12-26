package by.funduk.services

import by.funduk.db.*
import by.funduk.model.*
import by.funduk.ui.SubmissionView
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*

object SubmitService {

    suspend fun submitAndTest(submission: Submission): Int {
        val id = insert(submission)
        TestService.test(submission.copy(id = id))
        return id
    }

    private suspend fun insert(submission: Submission): Int = query {
        Submissions.insert {
            it[taskId] = submission.taskId
            it[userId] = submission.userId
            it[submitTime] = submission.submitTime.toJavaLocalDateTime()
            it[code] = submission.code
            it[language] = submission.language.toString()
            it[status] = submission.testInfo.status.toString()
            it[time] = submission.testInfo.time
            it[memory] = submission.testInfo.memory
        }[Submissions.id].value
    }

    suspend fun updateTestInfo(id: Int, info: TestInfo): Int = query {
        Submissions.update({ Submissions.id eq id }) {
            it[status] = info.status.toString()
            it[time] = info.time
            it[memory] = info.memory
            if (info.status != Status.Running) {
                it[test] = info.test
            }
        }
    }

    suspend fun getSubmission(id: Int): Submission? = query {
        Submissions.selectAll()
            .where { Submissions.id eq id }
            .map {
                val status = Status.valueOf(it[Submissions.status])
                val test = if (status == Status.Running) {
                    TestService.getCurrentTest(it[Submissions.id].value)
                } else {
                    it[Submissions.test]
                }
                Submission(
                    id = it[Submissions.id].value,
                    taskId = it[Submissions.taskId].value,
                    userId = it[Submissions.userId].value,
                    submitTime = it[Submissions.submitTime].toKotlinLocalDateTime(),
                    code = it[Submissions.code],
                    language = Language.valueOf(it[Submissions.language]),
                    testInfo = TestInfo(
                        status,
                        test,
                        it[Submissions.time],
                        it[Submissions.memory]
                    )
                )
            }
            .singleOrNull()
    }

    suspend fun getSubmissionView(id: Int): SubmissionView? = query {
        (Submissions innerJoin Tasks innerJoin Users).selectAll()
            .where { Submissions.id eq id }
            .map {
                getViewFromRow(it)
            }
            .singleOrNull()
    }

    suspend fun getSubmissionViews(taskId: Int, userId: Int, count: Int, offset: Int): List<SubmissionView> = query {
        (Submissions innerJoin Tasks innerJoin Users).selectAll().limit(count).offset(offset.toLong())
            .orderBy(Submissions.submitTime to SortOrder.DESC)
            .where { (Submissions.taskId eq taskId) and (Submissions.userId eq userId) }.mapNotNull {
                getViewFromRow(it)
            }
    }

    private fun getViewFromRow(row: ResultRow): SubmissionView {
        val status = Status.valueOf(row[Submissions.status])
        val test = if (status == Status.Running) {
            TestService.getCurrentTest(row[Submissions.id].value)
        } else {
            row[Submissions.test]
        }
        return SubmissionView(
            id = row[Submissions.id].value,
            taskId = row[Submissions.taskId].value,
            userId = row[Submissions.userId].value,
            taskName = row[Tasks.name],
            userName = row[Users.username],
            submitTime = row[Submissions.submitTime].toKotlinLocalDateTime(),
            language = Language.valueOf(row[Submissions.language]),
            testInfo = TestInfo(
                status,
                test,
                row[Submissions.time],
                row[Submissions.memory]
            )
        )
    }

    suspend fun delete(id: Int): Boolean {
        return query {
            Submissions.deleteWhere { Submissions.id eq id }
        } > 0
    }
}