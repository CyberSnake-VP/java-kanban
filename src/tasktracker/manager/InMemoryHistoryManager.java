package tasktracker.manager;

import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node> history = new HashMap<>();

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
        if (head == null) {
            return tasks;             // Проверяем существуют ли ноды?
        }
        if (head == tail) {
            tasks.add(head.task);
            return tasks;             // Если всего одна нода, то просто воз-ем задачу этой ноды
        }
        Node curNode = head;
        while (curNode != tail) {     // Начиная с головы двигаемся до хвоста
            tasks.add(curNode.task);  // Добавляем задачу в список
            curNode = curNode.next;   // Переключаемся на следующую ноду
        }
        tasks.add(tail.task);         // Добавляем в список задачу хвоста
        return tasks;
    }

    // Метод для удаления ноды из линксписка
    public void removeNode(Node node) {
        if (node == head) {
            head = head.next;         // Если нода - голова
            head.prev = null;
            return;
        }
        if (node == tail) {
            tail = tail.prev;         // Если нода - хвост
            tail.next = null;
            return;
        }
        // Перезаписываем ссылки prev и next ноды, чтобы ссылались друг на друга
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    // Удаляем историю из таблицы Map
    @Override
    public void remove(int id) {
        Node nodeForRemove = history.get(id);       // Получаем ноду из таблицы
        removeNode(nodeForRemove);                  // Удаляем ноду из связного списка(линксписка)
        history.remove(id);                         // Удаляем задачу из самой таблицы
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.containsKey(task.getId())) {
            Node repeatNode = history.get(task.getId());
            removeNode(repeatNode);
        }
        history.put(task.getId(), linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return getTask();       // Возвращаем список истории
    }

}
