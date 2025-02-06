package tasktracker.manager;

import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> history = new HashMap<>();

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

    // Метод для удаления ноды из линксписка
    public void removeNode(Node node) {
        if (head == tail) {            // Если нода голова и хвост, т.е. одна
            head = null;
            tail = null;
            return;
        }
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
        if (history.containsKey(id)) {
            Node nodeForRemove = history.get(id);       // Получаем ноду из таблицы
            removeNode(nodeForRemove);                  // Удаляем ноду из связного списка(линксписка)
            history.remove(id);                         // Удаляем задачу из самой таблицы
        }
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
        List<Task> tasks = new ArrayList<>();

        Node node = head;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;       // Возвращаем список истории
    }

}
