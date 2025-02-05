package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void init() {
        task = new Task("", "");
        historyManager = Managers.getDefaultHistory();

    }

    @Test
    void add() {
        // given
        Task expectedTask = new Task(task);
        Epic expectedEpic = new Epic("","");
        Subtask expectedSubtask = new Subtask("","", new Epic("",""));
        expectedTask.setId(1);
        expectedEpic.setId(2);
        expectedSubtask.setId(3);

        // do
        historyManager.add(expectedTask);
        historyManager.add(expectedEpic);
        historyManager.add(expectedSubtask);
        historyManager.add(expectedTask);

        // expect
        assertEquals(3, historyManager.getHistory().size(), "Неверная длина списка");
    }


    @Test
    void remove() {
        // given
        Task expectedTask = new Task(task);
        Epic expectedEpic = new Epic("","");
        Subtask expectedSubtask = new Subtask("","", new Epic("",""));
        expectedTask.setId(1);
        expectedEpic.setId(2);
        expectedSubtask.setId(3);

        // do
        historyManager.add(expectedTask);
        historyManager.add(expectedEpic);
        historyManager.add(expectedSubtask);
        historyManager.remove(expectedTask.getId());
        historyManager.remove(expectedEpic.getId());
        historyManager.remove(expectedSubtask.getId());
        historyManager.remove(10);


        // expect
        assertEquals(0, historyManager.getHistory().size(), "Неверная длина списка");;
    }

    @Test
    void addNull() {
        Task taskNull = null;

        historyManager.add(taskNull);
        final List<Task> history = historyManager.getHistory();

        assertEquals(0, history.size(), "Размер списка неверный");
    }

    @Test
    void getHistory() {
        Task expectedTask = new Task(task);
        Epic expectedEpic = new Epic("","");
        Subtask expectedSubtask = new Subtask("","", new Epic("",""));
        expectedTask.setId(1);
        expectedEpic.setId(2);
        expectedSubtask.setId(3);

        historyManager.add(expectedTask);
        historyManager.add(expectedEpic);
        historyManager.add(expectedSubtask);
        historyManager.add(expectedTask);
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "Неверный размер списка");
        assertEquals(expectedEpic, historyManager.getHistory().getFirst(), "Неверная последовательность задач");
    }

}
