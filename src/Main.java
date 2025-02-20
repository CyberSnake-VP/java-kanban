import tasktracker.manager.*;
import tasktracker.tasks.*;
import tasktracker.enumeration.Status;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;

public class Main {

    public static void main(String[] args) {

        FileBackedTaskManager fm = new FileBackedTaskManager(new File("./src/tasktracker/files/data.csv"));

        Task task1 = new Task("Задача1", "Действие");
        Task task2 = new Task("Задача2", "Действие", Status.IN_PROGRESS);
        Task task3 = new Task("Задача3", "Действие");
        fm.createTask(task1);
        fm.createTask(task2);

        Epic epic1 = new Epic("Эпик1", "Действие");
        Epic epic2 = new Epic("Эпик2", "Действие");
        Epic epic3 = new Epic("Эпик3", "Действие");
        fm.createEpic(epic1);
        fm.createEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача1", "Эпик1", epic1, Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Подзадача2", "Эпик1", epic1);
        Subtask subtask3 = new Subtask("Подзадача3", "Эпик2", epic2);
        Subtask subtask4 = new Subtask("Подзадача4", "Эпик2", epic2);
        fm.createSubtask(subtask1);
        fm.createSubtask(subtask2);
        fm.createSubtask(subtask3);

        FileBackedTaskManager fmBackup = FileBackedTaskManager.loadFromFile(new File("./src/tasktracker/files/data.csv"));
        System.out.println("Из файла");
        System.out.println(fmBackup.getTaskList());
        System.out.println(fmBackup.getEpicList());
        System.out.println(fmBackup.getSubtaskList());
        fmBackup.createTask(task3);
        fmBackup.createEpic(epic3);
        fmBackup.createSubtask(subtask4);
        System.out.println("Добавлены новые задачи");
        System.out.println(fmBackup.getTaskList());
        System.out.println(fmBackup.getEpicList());
        System.out.println(fmBackup.getSubtaskList());


    }
}