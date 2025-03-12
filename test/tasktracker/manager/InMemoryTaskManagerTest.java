package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;


public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    void init() {
        task = new Task("name", "description", LocalDateTime.now(), Duration.ofMinutes(0));
        epic = new Epic("name", "description");

        taskManager = Managers.getDefault();
    }

}

