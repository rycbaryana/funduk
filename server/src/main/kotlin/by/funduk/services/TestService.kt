package by.funduk.services

import by.funduk.model.Status
import by.funduk.model.StatusMessage
import by.funduk.model.Submission
import by.funduk.model.TestInfo
import kotlinx.coroutines.*
import java.util.*
import kotlin.time.Duration.Companion.seconds

object TestService {

    private val testingScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val testInfos = Collections.synchronizedMap(mutableMapOf<Int, TestInfo>())

    fun test(submission: Submission) = testingScope.launch {
        beginTesting(submission)
        updateTestInfo(submission, TestInfo(Status.Running, 1, 0, 0))
        delay(1.seconds)
        updateTestInfo(submission, TestInfo(Status.Running, 1, 0, 0))
        delay(3.seconds)
        updateTestInfo(submission, TestInfo(Status.Running, 5, 0, 0))
        delay(5.seconds)
        updateTestInfo(submission, TestInfo(Status.Running, 12, 0, 0))
        delay(4.seconds)
        updateTestInfo(submission, TestInfo(Status.Running, 26, 0, 0))
        delay(9.seconds)
        updateTestInfo(submission, TestInfo(Status.Running, 39, 0, 0))
        delay(1.seconds)
        updateTestInfo(submission, TestInfo(Status.OK, 40, 1234, 234567654))
        endTesting(submission)
    }

    private fun beginTesting(submission: Submission) {
        testInfos.put(submission.id!!, submission.testInfo)
    }

    private fun endTesting(submission: Submission) {
        testInfos.remove(submission.id!!)
    }

    private suspend fun updateTestInfo(submission: Submission, testInfo: TestInfo) {
        val id = submission.id!!
        testInfos[id] = testInfo
        SubmitService.updateTestInfo(id, testInfo)
        NotificationService.notify(submission.taskId, submission.userId, StatusMessage(testInfo))
    }

    fun getCurrentTest(submissionId: Int): Int = testInfos[submissionId]?.test ?: 0
}