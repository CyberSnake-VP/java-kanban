package tasktracker.manager;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();

    // Создаем внутренний класс Node для создания линк-узла задачи
    private Node head;
    private Node tail;

    private static class Node {
        private Node prev;
        private Task task;
        private Node next;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    @Override
    public void add(Task task) {
        if(task == null) {
            return;
        }
        history.add(task);                     // Добавляем задачу в историю
        if (history.size() > 10) {             // Проверка списка истории на лимит
            history.removeFirst();             // Удаляем первый элемент списка
        }

    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);       // Возвращаем копию списка истории

    }
}
