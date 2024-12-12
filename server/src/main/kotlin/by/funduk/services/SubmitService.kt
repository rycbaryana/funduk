package by.funduk.services

import by.funduk.db.Submissions
import by.funduk.db.query
import by.funduk.model.Submission
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import kotlinx.datetime.toJavaLocalDateTime

object SubmitService {

    suspend fun submitAndTest(submission: Submission) {
        insert(submission)
        TestService.test(submission)
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

    suspend fun delete(id: Int): Boolean {
        return query {
            Submissions.deleteWhere { Submissions.id eq id }
        } > 0
    }
}