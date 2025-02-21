package tasktracker.manager;

import tasktracker.enumeration.TypeTasks;
import tasktracker.exceptions.ManagerBackupException;
import tasktracker.exceptions.ManagerSaveException;
import tasktracker.enumeration.Status;
import tasktracker.tasks.*;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File data;

    public FileBackedTaskManager(File file) {
        this.data = file;
    }

    public File getData() {
        return data;
    }

    public void save() {
        String taskForWrite = getAllTasksToFile();
        try (Writer fw = new FileWriter(data)) {
            fw.write(taskForWrite);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл. " + e.getMessage());
        }
    }

    private String getAllTasksToFile() {
        StringBuilder total = new StringBuilder();
        String title = "id,type,name,status,description,epic";
        total.append(title).append("\n");

        for (Task task : tasks.values()) {
            String str = toString(task);
            total.append(str).append("\n");
        }
        for (Task epic : epics.values()) {
            String str = toString(epic);
            total.append(str).append("\n");
        }
        for (Task subtask : subtasks.values()) {
            String str = toString(subtask);
            total.append(str).append("\n");
        }
        return total.toString();
    }

    private String toString(Task task) {
        String id = Integer.toString(task.getId());
        String type = TypeTasks.TASK.name();
        String name = task.getName();
        String description = task.getDescription();
        String status = task.getStatus().toString();
        String epicId = "";

        if (task instanceof Epic) {
            type = TypeTasks.EPIC.name();
        } else if (task instanceof Subtask) {
            type = TypeTasks.SUBTASK.name();
            epicId = Integer.toString(((Subtask) task).getEpicId());
        }

        return String.join(",", id, type, name, status, description, epicId);
    }

    // метод для получения задач из строки
    private Task fromString(String value) {

        String[] splitStr = value.split(",");

        int id = Integer.parseInt(splitStr[0]);
        String type = splitStr[1];
        String name = splitStr[2];
        Status status = Status.valueOf(splitStr[3]);
        String description = splitStr[4];
        int epicId = 0;
        if (splitStr.length > 5) {
            epicId = Integer.parseInt(splitStr[5]);
        }
        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case "SUBTASK":
                Subtask subtask = new Subtask(name, description, epics.get(epicId), status);
                subtask.setId(id);
                epics.get(epicId).setSubtaskIdList(id);
                return subtask;
        }

        return null;
    }

    // метод для записи задач в таблицу
    private void putTaskInMaps(Task task) {
        if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            subtasks.put(task.getId(), (Subtask) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            int countId = 0;
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            List<String> listTask = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            for (String s : listTask) {
                if (!Character.isDigit(s.charAt(0))) {
                    continue;
                }
                Task task = fileBackedTaskManager.fromString(s);
                fileBackedTaskManager.putTaskInMaps(task);

                if (countId < task.getId()) {
                    countId = task.getId();
                }
            }
            fileBackedTaskManager.iteratorId.counterID = countId + 1;
            return fileBackedTaskManager;

        } catch (IOException e) {
            throw new ManagerBackupException("Ошибка при восстановлении данных из файла. " + e.getMessage());
        }
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public Task deleteTask(int id) {
        Task deleted = super.deleteTask(id);
        save();
        return deleted;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic deleted = super.deleteEpic(id);
        save();
        return deleted;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask deleted = super.deleteSubtask(id);
        save();
        return deleted;
    }

    @Override
    public ArrayList<Task> deleteTaskList() {
        ArrayList<Task> deletedList = super.deleteTaskList();
        save();
        return deletedList;
    }

    @Override
    public ArrayList<Epic> deleteEpicList() {
        ArrayList<Epic> deletedList = super.deleteEpicList();
        save();
        return deletedList;
    }

    @Override
    public ArrayList<Subtask> deleteSubtaskList() {
        ArrayList<Subtask> deletedList = super.deleteSubtaskList();
        save();
        return deletedList;
    }

    @Override
    public ArrayList<Subtask> deleteSubtaskList(Epic epic) {
        ArrayList<Subtask> deletedList = super.deleteSubtaskList(epic);
        save();
        return deletedList;
    }

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
        System.out.println("Загрузка из файла:");
        printTaskTest(fmBackup);

        System.out.println("Добавлены новые задачи:");
        fmBackup.createTask(task3);
        fmBackup.createEpic(epic3);
        fmBackup.createSubtask(subtask4);

        printTaskTest(fmBackup);


    }

    static void printTaskTest(FileBackedTaskManager fbm) {
        for (Task task : fbm.getTaskList()) {
            System.out.printf("%S %s %S %d || ", task.getName(), task.getDescription(), task.getStatus().name(), task.getId());
        }
        System.out.println();
        for (Epic epic : fbm.getEpicList()) {
            System.out.printf("%S %s %S %d || ", epic.getName(), epic.getDescription(), epic.getStatus().name(), epic.getId());
        }
        System.out.println();
        for (Subtask subtask : fbm.getSubtaskList()) {
            System.out.printf("%S %s %S %d || ", subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getId());
        }
        System.out.println("\n");
    }
}
