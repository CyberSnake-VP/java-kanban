package tasktracker.manager;

import tasktracker.exceptions.IntersectionsException;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    // Методы для Task
    Task createTask(Task task) throws IntersectionsException;

    Task updateTask(Task task) throws IntersectionsException;

    ArrayList<Task> getTaskList();

    Task getTask(int id);

    ArrayList<Task> deleteTaskList();

    Task deleteTask(int id);

    // Методы для Epic
    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    ArrayList<Epic> getEpicList();

    ArrayList<Subtask> getSubtaskListInEpic(Epic epic);

    Epic getEpic(int id);

    ArrayList<Epic> deleteEpicList();

    Epic deleteEpic(int id);

    // Методы для Subtask
    Subtask createSubtask(Subtask subtask) throws IntersectionsException;

    Subtask updateSubtask(Subtask subtask) throws IntersectionsException;

    ArrayList<Subtask> getSubtaskList();

    Subtask getSubtask(int id);

    ArrayList<Subtask> deleteSubtaskList();

    ArrayList<Subtask> deleteSubtaskList(Epic epic);

    Subtask deleteSubtask(int id);

    // История просмотров задач
    List<Task> getHistory();

    // Получение списка задач отсортированных по приоритету
    List<Task> getPrioritizedTasks();

}
