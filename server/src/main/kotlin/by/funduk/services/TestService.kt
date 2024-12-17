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

    private val currentTest = Collections.synchronizedMap(mutableMapOf<Int, Int>())

    fun test(submission: Submission) = testingScope.launch {
        val id = submission.id!!
        delay(1.seconds)
        SubmitService.updateTestInfo(id, TestInfo(Status.Running, 1, 0, 0))
        NotificationService.notify(submission.userId, submission.taskId, StatusMessage(Status.Running, 1))
        delay(3.seconds)
        SubmitService.updateTestInfo(id, TestInfo(Status.Running, 5, 0, 0))
        NotificationService.notify(submission.userId, submission.taskId, StatusMessage(Status.Running, 5))
        delay(5.seconds)
        SubmitService.updateTestInfo(id, TestInfo(Status.Running, 12, 0, 0))
        NotificationService.notify(submission.userId, submission.taskId, StatusMessage(Status.Running, 12))
        delay(4.seconds)
        SubmitService.updateTestInfo(id, TestInfo(Status.Running, 26, 0, 0))
        NotificationService.notify(submission.userId, submission.taskId, StatusMessage(Status.Running, 26))
        delay(9.seconds)
        SubmitService.updateTestInfo(id, TestInfo(Status.Running, 39, 0, 0))
        NotificationService.notify(submission.userId, submission.taskId, StatusMessage(Status.Running, 39))
        delay(1.seconds)
        SubmitService.updateTestInfo(id, TestInfo(Status.OK, 40, 1234, 234567654))
        NotificationService.notify(submission.userId, submission.taskId, StatusMessage(Status.OK))
    }

    fun getCurrentTest(submissionId: Int): Int = currentTest.getOrDefault(submissionId, 0)
}