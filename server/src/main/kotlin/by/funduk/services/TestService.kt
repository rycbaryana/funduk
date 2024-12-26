package by.funduk.services

import by.funduk.db.TestCases
import by.funduk.db.query
import by.funduk.model.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.io.File
import java.util.*
import kotlin.io.path.createTempDirectory

object TestService {

    private val testingScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val testInfos = Collections.synchronizedMap(mutableMapOf<Int, TestInfo>())

    fun test(submission: Submission) = testingScope.launch {
        try {
            beginTesting(submission)

            val tempDir = createTempDirectory("task${submission.taskId}").toFile()
            println(tempDir)

            val sourceFile = File(tempDir, "source.${submission.language.extensions[0]}")
            sourceFile.writeText(submission.code)

            val testCases = getTestCases(submission.taskId)
            val mem = (Runtime.getRuntime().totalMemory())
            when (submission.language) {
                Language.CPP23_GCC14 -> {
                    updateTestInfo(submission, TestInfo(status = Status.Running, test = 1))
                }
                Language.Python3 -> {
                    for ((i, testCase) in testCases.withIndex()) {
                        val (input, expected) = testCase
                        updateTestInfo(submission, TestInfo(status = Status.Running, test = i + 1))
                        val process = ProcessBuilder( "python3", "source.py")
                            .directory(tempDir)
                            .redirectInput(ProcessBuilder.Redirect.PIPE)
                            .redirectOutput(ProcessBuilder.Redirect.PIPE)
                            .start()
                        process.outputStream.bufferedWriter().use {
                            it.write(input.toString())
                            it.flush()
                        }
                        process.waitFor()
                        val output = process.inputStream.bufferedReader().readText().trim()
                        if (expected.toString() != output) {
                            println("Expected $expected, got $output")
                            updateTestInfo(submission, TestInfo(status = Status.WA, test = i + 1))
                            break
                        }
                    }
                }
            }
            if (isOk(submission)) {
                updateTestInfo(submission, TestInfo(status = Status.OK))
            }
        } finally {
            endTesting(submission)
        }

    }

    private fun beginTesting(submission: Submission) {
        testInfos[submission.id!!] = submission.testInfo
    }

    private fun endTesting(submission: Submission) {
        testInfos.remove(submission.id!!)
    }

    private fun isOk(submission: Submission): Boolean {
        return testInfos[submission.id!!]!!.status in setOf(Status.OK, Status.Running, Status.Queued)
    }

    private suspend fun updateTestInfo(submission: Submission, testInfo: TestInfo) {
        val id = submission.id!!
        testInfos[id] = testInfo
        SubmitService.updateTestInfo(id, testInfo)
        NotificationService.notify(submission.taskId, submission.userId, StatusMessage(id, testInfo))
    }

    suspend fun addTestCase(taskId: Int, testCase: TestCase) = query {
        TestCases.insert {
            it[TestCases.taskId] = taskId
            it[input] = testCase.input
            it[output] = testCase.output
        }
    }

    private suspend fun getTestCases(taskId: Int): List<TestCase> = query {
        TestCases.selectAll().where { TestCases.taskId eq taskId }.map {
            TestCase(it[TestCases.input], it[TestCases.output])
        }
    }

    fun getCurrentTest(submissionId: Int): Int = testInfos[submissionId]?.test ?: 0
}