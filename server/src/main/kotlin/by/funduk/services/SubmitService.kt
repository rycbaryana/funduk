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
        TestService.test(submission)
        return id
    }

    suspend fun insert(submission: Submission): Int = query {
        Submissions.insert {
            it[taskId] = submission.taskId
            it[userId] = submission.userId
            it[submitTime] = submission.submitTime.toJavaLocalDateTime()
            it[code] = submission.code
            it[language] = submission.language.toString()
        }[Submissions.id].value
    }

    //TODO updates test info in submission with id "id"
    suspend fun update(id: Int, info: TestInfo): Int = query {
        1
    }

    suspend fun getSubmission(id: Int): Submission? = query {
        (Submissions innerJoin TestInfos).selectAll()
            .where { Submissions.id eq id }
            .map {
                Submission(
                    id = it[Submissions.id].value,
                    taskId = it[Submissions.taskId].value,
                    userId = it[Submissions.userId].value,
                    submitTime = it[Submissions.submitTime].toKotlinLocalDateTime(),
                    code = it[Submissions.code],
                    language = Language.valueOf(it[Submissions.language]),
                    testInfo = TestInfo(
                        Status.valueOf(it[TestInfos.status]),
                        it[TestInfos.currentTest],
                        it[TestInfos.time],
                        it[TestInfos.memory]
                    )
                )
            }
            .singleOrNull()
    }

    suspend fun getSubmissionView(id: Int): SubmissionView? = query {
        (Submissions innerJoin TestInfos innerJoin Tasks innerJoin Users).selectAll()
            .where { Submissions.id eq id }
            .map {
                getViewFromRow(it)
            }
            .singleOrNull()
    }

    suspend fun getSubmissionViews(taskId: Int, userId: Int, count: Int, offset: Int): List<SubmissionView> = query {
        Submissions.selectAll().limit(count).offset(offset.toLong())
            .where { (Submissions.taskId eq taskId) and (Submissions.userId eq userId) }.mapNotNull {
                getViewFromRow(it)
            }
    }

    private fun getViewFromRow(row: ResultRow): SubmissionView = SubmissionView(
        id = row[Submissions.id].value,
        taskId = row[Submissions.taskId].value,
        userId = row[Submissions.userId].value,
        taskName = row[Tasks.name],
        userName = row[Users.username],
        submitTime = row[Submissions.submitTime].toKotlinLocalDateTime(),
        language = Language.valueOf(row[Submissions.language]),
        testInfo = TestInfo(
            Status.valueOf(row[TestInfos.status]),
            row[TestInfos.currentTest],
            row[TestInfos.time],
            row[TestInfos.memory]
        )
    )

    suspend fun delete(id: Int): Boolean {
        return query {
            Submissions.deleteWhere { Submissions.id eq id }
        } > 0
    }
}