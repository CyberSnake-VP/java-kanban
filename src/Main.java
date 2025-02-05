import tasktracker.manager.*;
import tasktracker.tasks.*;
import tasktracker.status.Status;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("Выпить чай", "Поставить чайник, на огонь и дождаться когда закипит...");
        Task task2 = new Task("Выпить какао", "Поставить чайник, на огонь и дождаться когда закипит...", Status.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);


        Epic epic1 = new Epic("Переезд", "Отправится домой");
        Epic epic2 = new Epic("Полет на марс", "Создание новой колонии, освоение планеты ...");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Купить билеты", "Выбрать авиакомпанию, подобрать даты...", epic1, Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Cобрать багаж", "Найти чемодан, сложить вещи...", epic1);
        Subtask subtask3 = new Subtask("Построить ракету", "Создание ракеты с максимальной полезной нагрузкой...", epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getTask(task1.getId());


        printAllTasks(taskManager);

        taskManager.createTask(null);
        taskManager.createSubtask(null);
        taskManager.createEpic(null);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("\n Эпики:");
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);

            for (Task task : manager.getSubtaskListInEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\n Подзадачи:");
        for (Task subtask : manager.getSubtaskList()) {
            System.out.println(subtask);
        }

        System.out.println("\n История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.printf("В истории: %d элементов.%n", manager.getHistory().size());
    }

}