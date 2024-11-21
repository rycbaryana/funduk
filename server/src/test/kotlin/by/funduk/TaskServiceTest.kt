package by.funduk

import by.funduk.internal.db.Tags
import by.funduk.internal.db.Tasks
import by.funduk.internal.db.TasksTags
import by.funduk.model.Rank
import by.funduk.model.Tag
import by.funduk.model.Task
import by.funduk.internal.services.TaskService
import by.funduk.ui.TaskView
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

val sampleTask = Task(
    null,
    "Test",
    "Test",
    Rank.Cow,
    listOf(Tag.DP, Tag.FFT),
    100
)

class TaskServiceTest : BaseServiceTest(Tasks, Tags, TasksTags) {
    @Test
    fun testAddTask() = runBlocking {

        val taskId = TaskService.add(sampleTask)
        val task = TaskService.get(taskId)

        assertNotNull(task)
        assertEquals(sampleTask.copy(id = taskId), task)
    }

    @Test
    fun testDeleteTask() = runBlocking {
        val taskId = TaskService.add(sampleTask)

        val isDeleted = TaskService.delete(taskId)
        assertTrue(isDeleted)

        val task = TaskService.get(taskId)
        assertNull(task)
    }

    @Test
    fun testGetViews() = runBlocking {
        val tasks = (0..<50).map {
            val task = Task(
                null, it.toString(), "Test", Rank.Calf
            )
            val id = TaskService.add(task)
            task.copy(id = id)
        }
        repeat(5) {
            val views = TaskService.getViews(10, it * 10)
            assertEquals(views.size, 10)
            assertEquals(tasks.subList(it * 10, it * 10 + 10).map { task ->
                TaskView(task)
            }, views)
        }
    }
}