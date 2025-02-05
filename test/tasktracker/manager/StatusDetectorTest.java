package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.status.Status;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class StatusDetectorTest {

    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    void initManagerAndEpic() {
        taskManager = Managers.getDefault();
        epic = taskManager.createEpic(new Epic("name", "description"));
    }

    @Test
    void setEpicStatusShouldBeNew() {
        taskManager.createSubtask(new Subtask("name", "description", epic, Status.DONE));
        taskManager.createSubtask(new Subtask("name", "description", epic, Status.NEW));

        StatusDetector.setEpicStatus(epic, taskManager.getSubtaskListInEpic(epic));

        assertEquals(Status.NEW, epic.getStatus());

    }

    @Test
    void setEpicStatusShouldBeDone() {
        taskManager.createSubtask(new Subtask("name", "description", epic, Status.DONE));

        StatusDetector.setEpicStatus(epic, taskManager.getSubtaskListInEpic(epic));

        assertEquals(Status.DONE, epic.getStatus());

    }

    @Test
    void setEpicStatusShouldBeInProgress() {
        taskManager.createSubtask(new Subtask("name", "description", epic, Status.NEW));
        taskManager.createSubtask(new Subtask("name", "description", epic, Status.DONE));
        taskManager.createSubtask(new Subtask("name", "description", epic, Status.IN_PROGRESS));

        StatusDetector.setEpicStatus(epic, taskManager.getSubtaskListInEpic(epic));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());

    }

}