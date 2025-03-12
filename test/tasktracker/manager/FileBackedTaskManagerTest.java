package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.IntersectionsException;
import tasktracker.exceptions.ManagerBackupException;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    FileBackedTaskManager manager;

    @BeforeEach
    public void initFileManager() {
        try {
            manager = new FileBackedTaskManager(File.createTempFile("test", ".csv"));
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл" + e.getMessage());
        }

    }

    @Test
    void shouldBeSaveAndLoadFromFile() throws IntersectionsException {
        // given
        Task expectedTask = manager.createTask(task);
        Epic expectedEpic = manager.createEpic(epic);
        Subtask expectedSubtask = manager.createSubtask(new Subtask("name", "description", expectedEpic, LocalDateTime.now(), Duration.ofMinutes(1)));
        Task expectedNewTask = new Task("newTask", "description", LocalDateTime.now(), Duration.ofMinutes(1));

        // do
        manager = FileBackedTaskManager.loadFromFile(manager.getData());
        Task actualTask = manager.getTask(expectedTask.getId());
        Epic actualEpic = manager.getEpic(expectedEpic.getId());
        Subtask actualSubtask = manager.getSubtask(expectedSubtask.getId());
        Task actualNewTaskWithId = manager.createTask(expectedNewTask);
        Task actualNewTask = manager.getTask(actualNewTaskWithId.getId());

        // expect
        assertNotNull(actualTask, "Задача не создана");
        assertNotNull(actualEpic, "Эпик не создан");
        assertNotNull(actualSubtask, "Подзадача не создана");
        assertNotNull(actualNewTask, "Новая задача не создана");
        assertEquals(expectedTask, actualTask, "Задачи не равны");
        assertEquals(expectedEpic, actualEpic, "Эпики не равны");
        assertEquals(expectedSubtask, actualSubtask, "Подзадачи не равны");
        assertEquals(actualNewTaskWithId, actualNewTask, "Новые задачи не равны");
    }

    @Test
    void shouldBeExceptionManagerBackupException() {
        assertThrows(ManagerBackupException.class, () -> {
            manager = FileBackedTaskManager.loadFromFile(new File("./task/task.csv"));
        }, "Должно быть исключение об ошибки чтения из файла.");
    }

}