package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.enumeration.Status;
import tasktracker.exceptions.ManagerValidationIsFailed;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;


import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierTest extends InMemoryTaskManagerTest {


    @Test
    void setEpicStatusShouldBeNew() {
        Epic epicTest = taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(1)));
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1)));

        Identifier.setEpicStatus(epicTest, taskManager.getSubtaskListInEpic(epicTest));

        assertEquals(Status.NEW, epicTest.getStatus());

    }

    @Test
    void setEpicStatusShouldBeDone() {
        Epic epicTest = taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(1)));

        Identifier.setEpicStatus(epic, taskManager.getSubtaskListInEpic(epic));

        assertEquals(Status.DONE, epic.getStatus());

    }

    @Test
    void setEpicStatusShouldBeInProgress() {
        Epic epicTest = taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1)));
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(1)));
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(1)));

        Identifier.setEpicStatus(epicTest, taskManager.getSubtaskListInEpic(epicTest));

        assertEquals(Status.IN_PROGRESS, epicTest.getStatus());

    }

}