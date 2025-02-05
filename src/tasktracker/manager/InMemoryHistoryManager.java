package tasktracker.manager;

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

    // Метод для добавления задачи в конец списка
    public Node linkLast(Task task) {
        Node oldTail = tail;
        Node newTail = new Node(tail, task, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        return newTail;
    }

    // Метод собирающий задачи в список ArrayList
    public List<Task> getTask() {
        List<Task> tasks = new ArrayList<>();
        if(head == null) {
            return tasks;             // Проверяем существуют ли ноды?
        }
        else if(head == tail) {
            tasks.add(head.task);
            return tasks;             // Если всего одна нода, то просто воз-ем задачу этой ноды
        }
        Node curNode = head;
        while (curNode != tail ) {    // Насчет
            tasks.add(curNode.task);
            curNode = curNode.next;
        }
        tasks.add(tail.task);
        return tasks;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
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
