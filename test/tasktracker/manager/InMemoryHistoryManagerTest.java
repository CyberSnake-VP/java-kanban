package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void init() {
        task = new Task("name", "description");
        historyManager = Managers.getDefaultHistory();

    }

    @Test
    void add() {
        final int limitHistory = 10;

        for (int i = 0; i < 15; i++) {
            historyManager.add(task);
        }
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не создана.");
        assertEquals(limitHistory, history.size(), "Размер списка не должен превышать 10");

    }

    @Test
    void getHistory() {
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Неверный размер списка");
        assertEquals(task, history.getFirst(), "Разные задачи");

    }

}
