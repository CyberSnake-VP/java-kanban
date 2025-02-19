import tasktracker.manager.*;
import tasktracker.tasks.*;
import tasktracker.status.Status;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("Выпить чай", "Поставить чайник, на огонь и дождаться когда закипит");
        Task task2 = new Task("Выпить какао", "Поставить чайник, на огонь и дождаться когда закипит", Status.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);


        Epic epic1 = new Epic("Переезд", "Отправится домой");
        Epic epic2 = new Epic("Полет на марс", "Создание новой колонии, освоение планеты");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Купить билеты", "Выбрать авиакомпанию, подобрать даты", epic1, Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Cобрать багаж", "Найти чемодан, сложить вещи", epic1);
        Subtask subtask3 = new Subtask("Построить ракету", "Создание ракеты с максимальной полезной нагрузкой", epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        FileBackedTaskManager fm = new FileBackedTaskManager(new File("src/tasktracker/files/data.csv"));

        fm.createTask(task1);
        fm.createTask(task2);
        fm.createEpic(epic1);
        fm.createEpic(epic2);
        fm.createSubtask(subtask1);
        fm.createSubtask(subtask2);
        fm.createSubtask(subtask3);





//        taskManager.getTask(task1.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getTask(task2.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getSubtask(subtask1.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getSubtask(subtask2.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getSubtask(subtask3.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getSubtask(subtask1.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getSubtask(subtask2.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getTask(task1.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getEpic(epic1.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getEpic(epic2.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.getTask(task1.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.deleteTask(task1.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();
//        taskManager.deleteEpic(epic1.getId());
//        for (Task tasks : taskManager.getHistory()) {
//            System.out.print(tasks.getName() + " | ");
//        }
//        System.out.println();

    }
}