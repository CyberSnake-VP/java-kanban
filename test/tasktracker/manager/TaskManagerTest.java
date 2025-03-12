package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import tasktracker.enumeration.Status;
import tasktracker.exceptions.IntersectionsException;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeEach
    abstract void init();

    public Subtask createSubtaskInEpic(Epic epicWithId) throws IntersectionsException{
        return taskManager.createSubtask(new Subtask("name", "description", epicWithId, LocalDateTime.now(), Duration.ofMinutes(0)));
    }

    @Test
    void createTask() throws IntersectionsException{
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
    void updateTask() throws IntersectionsException{
        // given
        Task taskTwo = new Task("name", "description", LocalDateTime.now(), Duration.ofMinutes(10));
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

        try {
            taskManager.updateTask(createdTask);
        } catch (tasktracker.exceptions.IntersectionsException e) {
            throw new RuntimeException(e);
        }
        try {
            taskManager.updateTask(createdTaskForTestingStatusDone);
        } catch (tasktracker.exceptions.IntersectionsException e) {
            throw new RuntimeException(e);
        }

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
    void getTaskList() throws IntersectionsException{
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
    void getTask() throws IntersectionsException{
        // given
        Task expectedTask = taskManager.createTask(task);

        // do
        Task actualTask = taskManager.getTask(expectedTask.getId());

        // expect
        assertNotNull(actualTask, "Задача не была создана");
        assertEquals(expectedTask, actualTask, "Разные задачи");
    }

    @Test
    void deleteTaskList() throws IntersectionsException{
        // given
        final int extendedTaskListSize = 0;

        // do
        taskManager.createTask(task);
        taskManager.deleteTaskList();

        // expect
        assertEquals(extendedTaskListSize, taskManager.getTaskList().size(), "Размер списка отличается");
    }

    @Test
    void deleteTask() throws IntersectionsException{
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
        assertNotNull(createdEpic, "Эпик не был обновлен");
        assertEquals(expectedName, actualEpicUpdate.getName(), "поле name не совпадает");
        assertEquals(expectedDescription, actualEpicUpdate.getDescription(), "поле description не совпадает");
        assertEquals(expectedStatus, actualEpicUpdate.getStatus(), "Статус не должен быть изменен самостоятельно");

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
    void getSubtaskListInEpic() throws IntersectionsException{
        // given
        final List<Subtask> expectedSubtaskList = new ArrayList<>();
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask expectedSubtask = createSubtaskInEpic(epicWithId);
        expectedSubtaskList.add(expectedSubtask);

        // do
        List<Subtask> actualSubtaskList = taskManager.getSubtaskListInEpic(epicWithId);

        // expect
        assertNotNull(actualSubtaskList, "Список подзадач не создан");
        assertEquals(expectedSubtaskList.size(), actualSubtaskList.size(), "Размеры списков подзадач не совпадают");
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
        assertEquals(expectedEpic, actualEpic, "Эпики разные");

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
    void createSubtask() throws IntersectionsException{
        // given
        final String expectedName = "expectedName";
        final String expectedDescription = "expectedDescription";
        final Status expectedStatus = Status.NEW;

        // do
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask expectedSubtask = taskManager.createSubtask(new Subtask(expectedName, expectedDescription, epicWithId, LocalDateTime.now(), Duration.ofMinutes(10)));
        Subtask actualSubtask = taskManager.getSubtask(expectedSubtask.getId());
        Subtask actualCopySubtaskMustBeNull = taskManager.createSubtask(expectedSubtask);

        // expect
        assertNotNull(actualSubtask, "Подзадача не была создана");
        assertNull(actualCopySubtaskMustBeNull, "Одна и та же подзадача не должен создаваться");
        assertEquals(expectedSubtask, actualSubtask, "Задачи не совпадают");
        assertEquals(expectedName, actualSubtask.getName(), "поле name не совпадает");
        assertEquals(expectedDescription, actualSubtask.getDescription(), "поле description не совпадает");
        assertEquals(expectedStatus, actualSubtask.getStatus(), "поле status не совпадает");

    }


    @Test
    void updateSubtask() throws IntersectionsException{
        // given
        final String expectedName = "expectedName";
        final String expectedDescription = "expectedDescription";
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask subtaskForTesting = createSubtaskInEpic(epicWithId);

        // do
        subtaskForTesting.setName(expectedName);
        subtaskForTesting.setDescription(expectedDescription);
        subtaskForTesting.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtaskForTesting);
        Subtask actualSubtaskAfterUpdate = taskManager.getSubtask(subtaskForTesting.getId());


        // expect
        assertNotNull(actualSubtaskAfterUpdate, "Подзадача не обновлена");
        assertEquals(expectedName, actualSubtaskAfterUpdate.getName(), "Поле name не совпадает");
        assertEquals(expectedDescription, actualSubtaskAfterUpdate.getDescription(), "Поле description не совпадает");
        assertEquals(Status.IN_PROGRESS, actualSubtaskAfterUpdate.getStatus(), "Поле статус не совпадает");

    }

    @Test
    void getSubtaskList() throws IntersectionsException{
        // given
        List<Subtask> expectedSubtasksList = new ArrayList<>();
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask expectedSubtask = createSubtaskInEpic(epicWithId);
        expectedSubtasksList.add(expectedSubtask);

        // do
        List<Subtask> actualSubtaskList = taskManager.getSubtaskList();

        // expect
        assertNotNull(actualSubtaskList, "Список не создан");
        assertEquals(expectedSubtasksList.size(), actualSubtaskList.size(), "Длина списков подзадач не совпадает");
        assertEquals(expectedSubtasksList.getFirst(), actualSubtaskList.getFirst(), "Подзадачи не равны");

    }

    @Test
    void getSubtask() throws IntersectionsException{
        // given
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask expectSubtask = createSubtaskInEpic(epicWithId);

        // do
        Subtask actualSubtask = taskManager.getSubtask(expectSubtask.getId());

        // expect
        assertNotNull(actualSubtask, "Подзадача не создана");
        assertEquals(expectSubtask, actualSubtask, "Подзадачи не равны");

    }

    @Test
    void deleteSubtaskList() throws IntersectionsException{
        // given
        final int expectedSizeList = 0;

        // do
        Epic epicWithId = taskManager.createEpic(epic);
        createSubtaskInEpic(epicWithId);
        taskManager.deleteSubtaskList();

        // expect
        assertEquals(expectedSizeList, taskManager.getSubtaskList().size(), "Размеры списка не совпадают");
    }


    @Test
    void deleteSubtask() throws IntersectionsException{
        // given
        Epic epicWithId = taskManager.createEpic(epic);
        Subtask createdSubtask = createSubtaskInEpic(epicWithId);

        // do
        taskManager.deleteSubtask(createdSubtask.getId());
        Subtask actualSubtaskAfterDeleted = taskManager.getSubtask(createdSubtask.getId());

        // expect
        assertNull(actualSubtaskAfterDeleted, "Подзадача не была удалена");

    }

    @Test
    void getHistory() throws IntersectionsException{
        // given
        Task createdTask = taskManager.createTask(task);
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask createdSubtask = createSubtaskInEpic(createdEpic);

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
    void shouldBeSavePreviousTaskVersionInHistoryManager() throws IntersectionsException{
        // given
        String expectedName = "name";
        String expectedDescription = "description";
        Status expectedStatus = Status.NEW;
        Task testingTask = taskManager.createTask(new Task(expectedName, expectedDescription, expectedStatus, LocalDateTime.now(), Duration.ofMinutes(1)));
        taskManager.getTask(testingTask.getId());

        // do
        testingTask.setName("otherName");
        testingTask.setDescription("otherDescription");
        testingTask.setStatus(Status.DONE);
        try {
            taskManager.updateTask(testingTask);
        } catch (tasktracker.exceptions.IntersectionsException e) {
            throw new RuntimeException(e);
        }

        List<Task> history = taskManager.getHistory();
        Task actualTaskInHistory = history.getFirst();

        // expect
        assertEquals(expectedName, actualTaskInHistory.getName(), "Имена не совпадают");
        assertEquals(expectedDescription, actualTaskInHistory.getDescription(), "Описание не совпадает");
        assertEquals(expectedStatus, actualTaskInHistory.getStatus(), "Статусы не совпадают");

    }

    @Test
    void shouldBeSaveActualVersionTaskWithoutChanceChangeWithUseSet() throws IntersectionsException{
        // given
        String expectedName = "name";
        String expectedDescription = "description";
        Status expectedStatus = Status.NEW;
        Task expectedTask = taskManager.createTask(new Task(expectedName, expectedDescription, expectedStatus, LocalDateTime.now(), Duration.ofMinutes(0)));

        // do
        expectedTask.setName("otherName");
        expectedTask.setDescription("otherDescription");
        expectedTask.setStatus(Status.DONE);
        Task actualTask = taskManager.getTask(expectedTask.getId());
        try {
            taskManager.updateTask(actualTask);
        } catch (tasktracker.exceptions.IntersectionsException e) {
            throw new RuntimeException(e);
        }
        actualTask.setName("otherName");
        actualTask.setDescription("otherDescription");
        actualTask.setStatus(Status.DONE);
        actualTask = taskManager.getTask(expectedTask.getId());


        // expect
        assertEquals(expectedName, actualTask.getName(), "Имена не совпадают");
        assertEquals(expectedDescription, actualTask.getDescription(), "Описание не совпадает");
        assertEquals(expectedStatus, actualTask.getStatus(), "Статусы не совпадают");

    }

    @Test
    void shouldBeNotSaveTaskWhenStartTimeOrEndTimeIntersectTimeCompletingOtherTasksInPriorityList() throws IntersectionsException{
        // given
        Task expectedTask = task;
        expectedTask.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        expectedTask.setDuration(Duration.ofMinutes(10));
        Task expectedTaskWithId = taskManager.createTask(expectedTask);

        // do
        Task actualTaskNotGoodTime1 = new Task("", "", LocalDateTime.of(2025, 1, 1, 12, 10), Duration.ofMinutes(10));
        taskManager.createTask(actualTaskNotGoodTime1);
        Task actualTaskNotGoodTime2 = new Task("", "", LocalDateTime.of(2025, 1, 1, 12, 20), Duration.ofMinutes(10));
        taskManager.createTask(actualTaskNotGoodTime2);

        // expect
        assertNotNull(taskManager.getPrioritizedTasks(), "Список не должен быть null");
        assertEquals(3, taskManager.getPrioritizedTasks().size(), "В списке приоритета должна быть ОДНА задача");
        assertEquals(expectedTaskWithId, taskManager.getPrioritizedTasks().getFirst(), "Задачи не равны");

    }


}



