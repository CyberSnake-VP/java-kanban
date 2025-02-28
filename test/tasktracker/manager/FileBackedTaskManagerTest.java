//package tasktracker.manager;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import tasktracker.tasks.Epic;
//import tasktracker.tasks.Subtask;
//import tasktracker.tasks.Task;
//
//import java.io.File;
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FileBackedTaskManagerTest {
//
//    private FileBackedTaskManager manager;
//
//    @BeforeEach
//    void init() {
//       try {
//           manager = new FileBackedTaskManager(File.createTempFile("test", ".csv"));
//       } catch (IOException e) {
//           System.out.println("Ошибка при записи в файл" + e.getMessage());
//       }
//
//    }
//
//
//    @Test
//    void shouldBeSaveAndLoadFromFile() {
//        // given
//        Task expectedTask = manager.createTask(new Task("task","description"));
//        Epic expectedEpic = manager.createEpic(new Epic("epic","description"));
//        Subtask expectedSubtask = manager.createSubtask(new Subtask("subtask","description", expectedEpic));
//        Task expectedNewTask = new Task("newTask", "description");
//
//        // do
//        manager = FileBackedTaskManager.loadFromFile(manager.getData());
//        Task actualTask = manager.getTask(expectedTask.getId());
//        Epic actualEpic = manager.getEpic(expectedEpic.getId());
//        Subtask actualSubtask = manager.getSubtask(expectedSubtask.getId());
//        Task actualNewTaskWithId = manager.createTask(expectedNewTask);
//        Task actualNewTask = manager.getTask(actualNewTaskWithId.getId());
//
//        // expect
//        assertNotNull(actualTask, "Задача не создана");
//        assertNotNull(actualEpic, "Эпик не создан");
//        assertNotNull(actualSubtask, "Подзадача не создана");
//        assertNotNull(actualNewTask, "Новая задача не создана");
//        assertEquals(expectedTask, actualTask, "Задачи не равны");
//        assertEquals(expectedEpic, actualEpic, "Эпики не равны");
//        assertEquals(expectedSubtask, actualSubtask, "Подзадачи не равны");
//        assertEquals(actualNewTaskWithId, actualNewTask, "Новые задачи не равны");
//    }
//
//}