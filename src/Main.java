import com.sun.security.jgss.GSSUtil;
import tasktracker.enumeration.Status;
import tasktracker.manager.HistoryManager;
import tasktracker.manager.Managers;
import tasktracker.manager.TaskManager;
import tasktracker.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task = new Task("задача1", "описаниеЗадачи1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(2323));
        Task task1 = new Task("задача1", "описаниеЗадачи1", Status.NEW, LocalDateTime.of(2025, 2, 1,11,42), Duration.ofMinutes(260));
        Task task2 = new Task("задача1", "описаниеЗадачи1", Duration.ofMinutes(23));

        Epic epic = new Epic("эпик1", "описаниеЭпика");

        manager.createTask(task);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epicWithId = manager.createEpic(epic);

        Subtask subtask = new Subtask("сабба", "описание", epicWithId, Status.IN_PROGRESS,LocalDateTime.parse("04.03.2025, 20:00", epicWithId.getFormatter()), Duration.ofMinutes(1000));
        Subtask subtask1 = new Subtask("подзадача2", "описание", epicWithId,  LocalDateTime.parse("01.03.2025, 12:00", epicWithId.getFormatter()), Duration.ofMinutes(1000));
        Subtask subtask2 = new Subtask("подзадача3", "описание", epicWithId,LocalDateTime.parse("05.03.2025, 15:00", epicWithId.getFormatter()), Duration.ofMinutes(2323));

        manager.createSubtask(subtask);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);


        for (Task t : manager.getTaskList()) {
            System.out.println(t);
        }
        System.out.println();
        for(Task t : manager.getEpicList()) {
            System.out.println(t);
        }
        System.out.println();

        for(Task t : manager.getSubtaskList()) {
            System.out.println(t);
        }
        System.out.println();
        System.out.println("После удаления подзадачи");
        System.out.println();

        manager.deleteSubtask(subtask1.getId());

        for(Task t : manager.getEpicList()) {
            System.out.println(t);
        }
        System.out.println();
        for(Task t : manager.getSubtaskList()) {
            System.out.println(t);
        }
        System.out.println();
        manager.deleteSubtask(subtask.getId());
        for(Task t : manager.getEpicList()) {
            System.out.println(t);
        }
        System.out.println();
        for(Task t : manager.getSubtaskList()) {
            System.out.println(t);
        }
        manager.deleteSubtaskList();
        for(Task t : manager.getEpicList()) {
            System.out.println(t);
        }
        System.out.println();
        for(Task t : manager.getSubtaskList()) {
            System.out.println(t);
        }
        System.out.println();
    }
}