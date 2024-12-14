package by.funduk.services

import by.funduk.model.Status
import by.funduk.model.StatusMessage
import by.funduk.model.Submission
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

object TestService {

    private val testingScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun test(submission: Submission) = testingScope.launch {
        NotificationService.notify(submission.userId, submission.taskId, StatusMessage(Status.Running))
        delay(5.seconds)
        NotificationService.notify(submission.userId, submission.taskId, StatusMessage(Status.OK))
    }
}