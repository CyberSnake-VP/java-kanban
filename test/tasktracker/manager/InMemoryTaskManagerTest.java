package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.status.Status;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private Task task;
    private Epic epic;

    @BeforeEach
    public void init() {
        task = new Task("name", "description");
        epic = new Epic("name", "description");

        taskManager = Managers.getDefault();
    }


    @Test
    void createTask() {
        // given
        final String expectedName = "name";
        final String expectedDescription = "description";
        final Status expectedStatus = Status.NEW;

        // do
        Task expectedTask = taskManager.createTask(task);
        Task actualTask = taskManager.getTask(expectedTask.getId());
        Task actualTaskCopyMustBeNull = taskManager.createTask(expectedTask);

        final String actualName = actualTask.getName();
        final String actualDescription = actualTask.getDescription();
        final Status actualStatus = actualTask.getStatus();

        // expect
        assertNotNull(actualTask, "Задача не создана.");
        assertNull(actualTaskCopyMustBeNull, "Одинаковая задача не должна быть записана");
        assertEquals(expectedTask, actualTask, "Задачи не совпадают.");
        assertEquals(expectedName, actualName, "Поле name не совпадает");
        assertEquals(expectedDescription, actualDescription, "Поле description не совпадает");
        assertEquals(expectedStatus, actualStatus, "Не совпадает статус задачи");

    }

    @Test
    void updateTask() {
        // given
        Task taskTwo = new Task("name", "description");
        Task createdTask = taskManager.createTask(task);
        Task createdTaskForTestingStatusDone = taskManager.createTask(taskTwo);

        final String expectedName = "expectedName";
        final String expectedDescription = "expectedDescription";
        final Status expectedStatusInProgress = Status.IN_PROGRESS;
        final Status expectedStatusDone = Status.DONE;

        // do
        createdTask.setName(expectedName);
        createdTask.setDescription(expectedDescription);
        createdTask.setStatus(expectedStatusInProgress);
        createdTaskForTestingStatusDone.setStatus(expectedStatusDone);

        taskManager.updateTask(createdTask);
        taskManager.updateTask(createdTaskForTestingStatusDone);

        Task actualUpdatedTask = taskManager.getTask(createdTask.getId());
        Task actualUpdatedTaskWithStatusDone = taskManager.getTask(createdTaskForTestingStatusDone.getId());

        // expect
        assertNotNull(actualUpdatedTask, "Задача не была обновлена");
        assertEquals(expectedName, actualUpdatedTask.getName(), "Поле name не было изменено");
        assertEquals(expectedDescription, actualUpdatedTask.getDescription(), "Поле description не было изменено");
        assertEquals(expectedStatusInProgress, actualUpdatedTask.getStatus(), "Статус должен быть InProgress");
        assertEquals(expectedStatusDone, actualUpdatedTaskWithStatusDone.getStatus(), "Статус должен быть Done");
    }

    @Test
    void getTaskList() {
        // given
        List<Task> expectedTasksList = new ArrayList<>();
        Task taskWithId = taskManager.createTask(task);
        expectedTasksList.add(taskWithId);

        // do
        List<Task> actualTasksList = taskManager.getTaskList();

        // expect
        assertNotNull(actualTasksList, "Список отсутствует");
        assertEquals(expectedTasksList.size(), actualTasksList.size(), "Размер списка отличается");
        assertEquals(expectedTasksList.getFirst(), actualTasksList.getFirst(), "Задачи в списке отличаются");
    }

    @Test
    void getTask() {
        // given
        Task expectedTask = taskManager.createTask(task);

        // do
        Task actualTask = taskManager.getTask(expectedTask.getId());

        // expect
        assertNotNull(actualTask, "Задача не была создана");
        assertEquals(expectedTask, actualTask, "Разные задачи");
    }

    @Test
    void deleteTaskList() {
        // given
       final int extendedTaskListSize = 0;

        // do
        taskManager.createTask(task);
        taskManager.deleteTaskList();

        // expect
        assertEquals(extendedTaskListSize, taskManager.getTaskList().size(), "Размер списка отличается");
    }

    @Test
    void deleteTask() {
        // given
        Task createdTaskWithId = taskManager.createTask(task);

        // do
        taskManager.deleteTask(createdTaskWithId.getId());
        Task actualTask = taskManager.getTask(createdTaskWithId.getId());

        // expect
        assertNull(actualTask, "Задача не была удалена");

    }

    @Test
    void createEpic() {
        // given
        final String expectedName = "name";
        final String expectedDescription = "description";
        final Status expectedStatus = Status.NEW;

        // do
        Epic expectedEpic = taskManager.createEpic(epic);
        Epic actualEpic = taskManager.getEpic(expectedEpic.getId());
        Epic actualEpicCopyMustBeNull = taskManager.createEpic(expectedEpic);

        final String actualName = actualEpic.getName();
        final String actualDescription = actualEpic.getDescription();
        final Status actualStatus = actualEpic.getStatus();

        // expect
        assertNotNull(actualEpic, "Эпик не создан.");
        assertNull(actualEpicCopyMustBeNull, "Одинаковый эпик не должен быть записан");
        assertEquals(expectedEpic, actualEpic, "Эпики не совпадают.");
        assertEquals(expectedName, actualName, "Поле name не совпадает");
        assertEquals(expectedDescription, actualDescription, "Поле description не совпадает");
        assertEquals(expectedStatus, actualStatus, "Не совпадает статус");

    }

    @Test
    void updateEpic() {
        // given
        Epic createdEpic = taskManager.createEpic(epic);

        final String expectedName = "expectedName";
        final String expectedDescription = "expectedDescription";
        final Status expectedStatus = Status.NEW;

        // do
        createdEpic.setName(expectedName);
        createdEpic.setDescription(expectedDescription);
        createdEpic.setStatus(Status.IN_PROGRESS);

        taskManager.updateEpic(createdEpic);
        Epic actualEpicUpdate = taskManager.getEpic(createdEpic.getId());

        // expect
        assertNotNull(createdEpic,"Эпик не был обновлен");
        assertEquals(expectedName, actualEpicUpdate.getName(),"поле name не совпадает");
        assertEquals(expectedDescription, actualEpicUpdate.getDescription(),"поле description не совпадает");
        assertEquals(expectedStatus, actualEpicUpdate.getStatus(),"Статус не должен быть изменен самостоятельно");

    }

    @Test
    void getEpicList() {
        // given
        List<Epic> expectedEpicList = new ArrayList<>();
        Epic epicWithId = taskManager.createEpic(epic);
        expectedEpicList.add(epicWithId);

        // do
        List<Epic> actualEpicList = taskManager.getEpicList();

        // expect
        assertNotNull(actualEpicList, "Список не создан");
        assertEquals(expectedEpicList.size(), actualEpicList.size(), "Размер списка отличается");
        assertEquals(expectedEpicList.getFirst(), actualEpicList.getFirst(), "Эпики в списке отличаются");

    }

    @Test
    void getSubtaskListInEpic() {
        // given
        final List<Subtask> expectedSubtaskList = new ArrayList<>();
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask expectedSubtask = taskManager.createSubtask(new Subtask("name", "description", epicWithId));
        expectedSubtaskList.add(expectedSubtask);

        // do
        List<Subtask> actualSubtaskList = taskManager.getSubtaskListInEpic(epicWithId);

        // expect
        assertNotNull(actualSubtaskList, "Список подзадач не создан");
        assertEquals(expectedSubtaskList.size(),actualSubtaskList.size(), "Размеры списков подзадач не совпадают");
        assertEquals(expectedSubtaskList.getFirst(), actualSubtaskList.getFirst(), "Подзадачи отличаются");

    }

    @Test
    void getEpic() {
        // given
        Epic expectedEpic = taskManager.createEpic(epic);

        // do
        Epic actualEpic = taskManager.getEpic(expectedEpic.getId());

        // expect
        assertNotNull(actualEpic, "Эпик не был создан");
        assertEquals(expectedEpic, actualEpic,"Эпики разные");

    }

    @Test
    void deleteEpicList() {
        // given
       final int expectedEpicListSize = 0;

        // do
        taskManager.createEpic(epic);
        taskManager.deleteEpicList();

        // expect
        assertEquals(expectedEpicListSize, taskManager.getEpicList().size(), "Размер списка отличается");
    }

    @Test
    void deleteEpic() {
        // given
        Epic epicWithId = taskManager.createEpic(epic);

        // do
        Epic actualEpic = taskManager.getEpic(epicWithId.getId());
        assertEquals(actualEpic, epicWithId, "Эпик не был создан");

        taskManager.deleteEpic(epicWithId.getId());
        Epic actualEpicAfterDeleted = taskManager.getEpic(epicWithId.getId());

        // expect
        assertNull(actualEpicAfterDeleted, "Эпик не был удален");

    }

    @Test
    void createSubtask() {
        // given
        final String expectedName = "expectedName";
        final String expectedDescription = "expectedDescription";
        final Status expectedStatus = Status.NEW;

        // do
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask expectedSubtask = taskManager.createSubtask(new Subtask(expectedName, expectedDescription, epicWithId));
        Subtask actualSubtask = taskManager.getSubtask(expectedSubtask.getId());
        Subtask actualCopySubtaskMustBeNull = taskManager.createSubtask(expectedSubtask);

        Epic actualEpic = taskManager.getEpic(epicWithId.getId());
        assertEquals(Status.NEW, actualEpic.getStatus(), "Статус у эпика не верный");
        taskManager.createSubtask(new Subtask(expectedName, expectedDescription, epicWithId, Status.IN_PROGRESS));
        taskManager.createSubtask(new Subtask(expectedName, expectedDescription, epicWithId, Status.DONE));
        actualEpic = taskManager.getEpic(epicWithId.getId());
        assertEquals(Status.IN_PROGRESS, actualEpic.getStatus(), "Статус у эпика не верный");

        // expect
        assertNotNull(actualSubtask, "Подзадача не была создана");
        assertNull(actualCopySubtaskMustBeNull, "Одна и та же подзадача не должен создаваться");
        assertEquals(expectedSubtask, actualSubtask, "Задачи не совпадают");
        assertEquals(expectedName, actualSubtask.getName(), "поле name не совпадает");
        assertEquals(expectedDescription, actualSubtask.getDescription(), "поле description не совпадает");
        assertEquals(expectedStatus, actualSubtask.getStatus(), "поле status не совпадает");

    }


    @Test
    void updateSubtask() {
        // given
        final String expectedName = "expectedName";
        final String expectedDescription = "expectedDescription";
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask subtaskForTesting = taskManager.createSubtask(new Subtask("name", "description", epicWithId));

        // do
        subtaskForTesting.setName(expectedName);
        subtaskForTesting.setDescription(expectedDescription);
        subtaskForTesting.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtaskForTesting);
        Subtask actualSubtaskAfterUpdate = taskManager.getSubtask(subtaskForTesting.getId());


        // expect
        assertNotNull(actualSubtaskAfterUpdate,"Подзадача не обновлена");
        assertEquals(expectedName, actualSubtaskAfterUpdate.getName(), "Поле name не совпадает");
        assertEquals(expectedDescription, actualSubtaskAfterUpdate.getDescription(), "Поле description не совпадает");
        assertEquals(Status.IN_PROGRESS, actualSubtaskAfterUpdate.getStatus(), "Поле статус не совпадает");

    }

    @Test
    void getSubtaskList() {
        // given
        List<Subtask> expectedSubtasksList = new ArrayList<>();
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask expectedSubtask = taskManager.createSubtask(new Subtask("name", "description", epicWithId));
        expectedSubtasksList.add(expectedSubtask);

        // do
        List<Subtask> actualSubtaskList = taskManager.getSubtaskList();

        // expect
        assertNotNull(actualSubtaskList, "Список не создан");
        assertEquals(expectedSubtasksList.size(),actualSubtaskList.size(), "Длина списков подзадач не совпадает");
        assertEquals(expectedSubtasksList.getFirst(), actualSubtaskList.getFirst(), "Подзадачи не равны");

    }

    @Test
    void getSubtask() {
        // given
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask expectSubtask = taskManager.createSubtask(new Subtask("name", "description", epicWithId));

        // do
        Subtask actualSubtask = taskManager.getSubtask(expectSubtask.getId());

        // expect
        assertNotNull(actualSubtask, "Подзадача не создана");
        assertEquals(expectSubtask, actualSubtask, "Подзадачи не равны");

    }

    @Test
    void deleteSubtaskList() {
        // given
        final int expectedSizeList = 0;

        // do
        Epic epicWithId = taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("name", "description", epicWithId));
        taskManager.deleteSubtaskList();

        // expect
        assertEquals(expectedSizeList, taskManager.getSubtaskList().size(), "Размеры списка не совпадают");
    }


    @Test
    void deleteSubtask() {
        // given
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask createdSubtask = taskManager.createSubtask(new Subtask("name", "description", epicWithId));

        // do
        taskManager.deleteSubtask(createdSubtask.getId());
        Subtask actualSubtaskAfterDeleted = taskManager.getSubtask(createdSubtask.getId());

        // expect
        assertNull(actualSubtaskAfterDeleted, "Подзадача не была удалена");

    }

    @Test
    void getHistory() {
        // given
        Task createdTask = taskManager.createTask(task);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask createdSubtask = taskManager.createSubtask(new Subtask("name", "description", createdEpic));

        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(createdTask);
        expectedHistory.add(createdEpic);
        expectedHistory.add(createdSubtask);

        // do
        taskManager.getTask(createdTask.getId());
        taskManager.getEpic(createdEpic.getId());
        taskManager.getSubtask(createdSubtask.getId());

        List<Task> actualHistory = taskManager.getHistory();

        // expect
        assertNotNull(actualHistory, "Список не создан");
        assertEquals(expectedHistory.size(), actualHistory.size(), "Размер списков отличается");
        assertEquals(expectedHistory.getFirst(), actualHistory.getFirst(), "История отличается");
        assertEquals(expectedHistory.get(1), actualHistory.get(1), "История отличается");
        assertEquals(expectedHistory.getLast(), actualHistory.getLast(), "История отличается");
    }

    @Test
    void shouldBeSavePreviousTaskVersionInHistoryManager() {
        // given
        String expectedName = "name";
        String expectedDescription = "description";
        Status expectedStatus = Status.NEW;
        Task testingTask = taskManager.createTask(new Task(expectedName, expectedDescription, expectedStatus));
        taskManager.getTask(testingTask.getId());

        // do
        testingTask.setName("otherName");
        testingTask.setDescription("otherDescription");
        testingTask.setStatus(Status.DONE);
        taskManager.updateTask(testingTask);

        List<Task> history = taskManager.getHistory();
        Task actualTaskInHistory = history.getFirst();

        // expect
        assertEquals(expectedName, actualTaskInHistory.getName(), "Имена не совпадают");
        assertEquals(expectedDescription, actualTaskInHistory.getDescription(), "Описание не совпадает");
        assertEquals(expectedStatus, actualTaskInHistory.getStatus(), "Статусы не совпадают");

    }

}

