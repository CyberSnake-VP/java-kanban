package tasktracker.manager;

import org.junit.jupiter.api.Test;
import tasktracker.enumeration.Status;
import tasktracker.exceptions.IntersectionsException;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;


import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierTest extends InMemoryTaskManagerTest {


    @Test
    void setEpicStatusShouldBeNew() throws IntersectionsException {
        Epic epicTest = taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(0)));
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(0)));

        Identifier.setEpicStatus(epicTest, taskManager.getSubtaskListInEpic(epicTest));

        assertEquals(Status.IN_PROGRESS, epicTest.getStatus());

    }

    @Test
    void setEpicStatusShouldBeDone() throws IntersectionsException{
        Epic epicTest = taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(0)));

        Identifier.setEpicStatus(epic, taskManager.getSubtaskListInEpic(epic));

        assertEquals(Status.DONE, epic.getStatus());

    }

    @Test
    void setEpicStatusShouldBeInProgress() throws IntersectionsException{
        Epic epicTest = taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(0)));
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(0)));
        taskManager.createSubtask(new Subtask("name", "description", epicTest, Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(0)));

        Identifier.setEpicStatus(epicTest, taskManager.getSubtaskListInEpic(epicTest));

        assertEquals(Status.IN_PROGRESS, epicTest.getStatus());

    }

}