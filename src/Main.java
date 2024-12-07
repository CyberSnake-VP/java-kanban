import tasktracker.tasks.*;
import tasktracker.status.Status;
import tasktracker.taskmanager.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Выпить чай", "Поставить чайник, на огонь и дождаться когда закипит...", Status.NEW);
        Task task2 = new Task("Выпить какао", "Поставить чайник, на огонь и дождаться когда закипит...", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Переезд", "Отправится домой");
        Epic epic2 = new Epic("Полет на марс", "Создание новой колонии, освоение планеты ...");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Купить билеты", "Выбрать авиакомпанию, подобрать даты...", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Cобрать багаж", "Найти чемодан, сложить вещи...", Status.NEW, epic1);
        Subtask subtask3 = new Subtask("Построить ракету", "Создание ракеты с максимальной полезной нагрузкой...", Status.NEW, epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        epic1 = taskManager.getEpicById(epic1.getId());
        epic2 = taskManager.getEpicById(epic2.getId());

        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println("-".repeat(300));

        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        epic1.setStatus(Status.IN_PROGRESS);
        epic2.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);

        subtask1.setStatus(Status.IN_PROGRESS );
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println("-".repeat(300));

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic2.getId());
        taskManager.deleteSubtask(subtask1.getId());

        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println("-".repeat(300));

    }
}