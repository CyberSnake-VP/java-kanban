package tasktracker.manager;

import tasktracker.enumeration.Type;
import tasktracker.exceptions.ManagerBackupException;
import tasktracker.exceptions.ManagerSaveException;
import tasktracker.enumeration.Status;
import tasktracker.tasks.*;

import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    // метод для записи в файл
    public void save() {
        String taskForWrite = getAllTasksToFile();   // Получаем подготовленный файл для записи со всеми задачами, тип String
        try (Writer fw = new FileWriter(data)) {
            fw.write(taskForWrite);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл. " + e.getMessage());
        }
    }

    // собираем все задачи из таблиц в строку
    private String getAllTasksToFile() {
        StringBuilder result = new StringBuilder();
        String title = "id,type,name,status,description,epic,startTime,duration,endTime";
        result.append(title).append("\n");       // строка заголовок, будет самой первой

        for (Task task : tasks.values()) {
            String str = toString(task);        // переводим задачу в строчное представление
            result.append(str).append("\n");
        }
        for (Task epic : epics.values()) {
            String str = toString(epic);
            result.append(str).append("\n");
        }
        for (Task subtask : subtasks.values()) {
            String str = toString(subtask);
            result.append(str).append("\n");
        }
        return result.toString();
    }

    // переводим задачу в строчное представление по определенному шаблону(через метод join собираем строку) для записи в файл .csv
    private String toString(Task task) {
        String id = Integer.toString(task.getId());
        String type = task.getType().name();     // получаем тип задачи с помощью Enum переменной
        String name = task.getName();
        String description = task.getDescription();
        String status = task.getStatus().toString();
        String epicId = "";
        String startTime = task.getStartTimeToString();
        String duration = task.getDurationToString();
        String endTime = task.getEndTimeToString();
        if (type.equals(Type.SUBTASK.name())) {
            epicId = Integer.toString(((Subtask) task).getEpicId());
        }

        return String.join(",", id, type, name, status, description, epicId, startTime, duration, endTime);
    }

    // метод для получения задач из строки
    private Task fromString(String value) {
        // Собираем строку в массив, получаем значение полей, проверяя на null, для методов parse
        String[] splitStr = value.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm");
        int id = Integer.parseInt(splitStr[0]);
        String type = splitStr[1];
        String name = splitStr[2];
        Status status = Status.valueOf(splitStr[3]);
        String description = splitStr[4];
        int epicId = (splitStr[5].isBlank())? 0 : Integer.parseInt(splitStr[5]);
        LocalDateTime startTime = (splitStr[6].equals("null"))? null : LocalDateTime.parse(splitStr[6], formatter);
        Duration duration = (splitStr[7].equals("null"))? null : Duration.ofMinutes(Integer.parseInt(splitStr[7]));
        LocalDateTime endTime = (splitStr[8].equals("null"))? null : LocalDateTime.parse(splitStr[8], formatter);

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status, startTime, duration);
                task.setId(id);
                addTaskInPriority(task);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                epic.setEndTime(endTime);
                return epic;
            case "SUBTASK":
                Subtask subtask = new Subtask(name, description, epics.get(epicId), status,startTime, duration);
                subtask.setId(id);
                epics.get(epicId).setSubtaskIdList(id);
                addTaskInPriority(subtask);
                return subtask;
        }

        return null;
    }

    // метод для записи задач в таблицу
    private void putTaskInMaps(Task task) {
        Type type = task.getType();   // получаем тип задачи
        switch (type) {               // кладем задачи в таблицу в соответствии с типом
            case Type.TASK -> tasks.put(task.getId(), task);
            case Type.EPIC -> epics.put(task.getId(), (Epic) task);
            case Type.SUBTASK -> subtasks.put(task.getId(), (Subtask) task);
        }
    }

//     Метод для создания объекта FileBackedTaskManager с готовыми задачами из Файла
    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            int countId = 0;     // счетчик для генерации Id для будущий задач.
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            List<String> listTask = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            for (String s : listTask) {
                if (!Character.isDigit(s.charAt(0))) {   // Проверка первого символа строки, если не число,
                    continue;                            // то значит это строка заголовок
                }
                Task task = fileBackedTaskManager.fromString(s);  // Получаем задачи из строки
                fileBackedTaskManager.putTaskInMaps(task);        // Записываем задачи в таблицы

                if (countId < task.getId()) {
                    countId = task.getId();      // находим самый большой id
                }
            }
            //записываем нужное начальное значение в поле counterId объекта iteratorId
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

        Task task1 = new Task("Задача1", "Действие", null, Duration.ofMinutes(2232));
        Task task2 = new Task("Задача2", "Действие", Status.IN_PROGRESS, LocalDateTime.of(2025, 3, 24, 23,20),Duration.ofMinutes(344));
        Task task3 = new Task("Задача3", "Действие", Status.IN_PROGRESS, LocalDateTime.of(2025, 4, 22, 3,20),Duration.ofMinutes(1511));
        fm.createTask(task1);
        fm.createTask(task2);

        Epic epic1 = new Epic("Эпик1", "Действие");
        Epic epic2 = new Epic("Эпик2", "Действие");
        Epic epic3 = new Epic("Эпик3", "Действие");
        fm.createEpic(epic1);
        fm.createEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача1", "Эпик1", epic1, Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(23232));
        Subtask subtask2 = new Subtask("Подзадача2", "Эпик1", epic1, Duration.ofMinutes(343434));
        Subtask subtask3 = new Subtask("Подзадача3", "Эпик2", epic2, LocalDateTime.of(2026, 3, 5, 1, 45),Duration.ofMinutes(3434));

        fm.createSubtask(subtask1);
        fm.createSubtask(subtask2);
        fm.createSubtask(subtask3);

        System.out.println("СОЗДАННЫЕ ЗАДАЧИ:");
        printTaskTest(fm);

        FileBackedTaskManager fmBackup = FileBackedTaskManager.loadFromFile(new File("./src/tasktracker/files/data.csv"));
        System.out.println("ЗАГРУЗКА ИЗ ФАЙЛА:");

        printTaskTest(fmBackup);

        fmBackup.createTask(task3);
        Epic epicWithID = fmBackup.createEpic(epic3);
        Subtask subtask4 = new Subtask("Подзадача4", "Эпик4", epicWithID, Duration.ofMinutes(5535));
        fmBackup.createSubtask(subtask4);

        System.out.println("ДОБАВЛЕНИЕ НОВЫХ ЗАДАЧ:");
        fmBackup.createTask(new Task("задача4", "описание", LocalDateTime.now(), Duration.ofMinutes(332)));
        printTaskTest(fmBackup);

        System.out.println("ПРИОРИТЕТ ЗАДАЧ НА ВЫПОЛНЕНИЕ");
        for (Task task : fmBackup.getPrioritizedTasks()) {
            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d |старт: %-15s | %-7s минут | завершение: %-15s \n",
                    task.getName(), task.getDescription(), task.getStatus().name(), task.getId(),
                    task.getStartTimeToString(), task.getDurationToString(), task.getEndTimeToString());
        }

    }
    static void printTaskTest(FileBackedTaskManager fbm) {
        for (Task task : fbm.getTaskList()) {
            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d |старт: %-15s | %-7s минут | завершение: %-15s \n",
                    task.getName(), task.getDescription(), task.getStatus().name(), task.getId(),
                    task.getStartTimeToString(), task.getDurationToString(), task.getEndTimeToString());
        }
        System.out.println();
        for (Epic epic : fbm.getEpicList()) {
            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d |старт: %-15s | %-7s минут | завершение: %-15s \n",
                    epic.getName(), epic.getDescription(), epic.getStatus().name(), epic.getId(),
                    epic.getStartTimeToString(), epic.getDurationToString(), epic.getEndTimeToString());
        }
        System.out.println();
        for (Subtask subtask : fbm.getSubtaskList()) {
            System.out.printf("%-10S | %-8s | статус: %-12S | id%-2d |старт: %-15s | %-7s минут | завершение: %-15s \n",
                    subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getId(),
                    subtask.getStartTimeToString(), subtask.getDurationToString(), subtask.getEndTimeToString());
        }
        System.out.println("\n");

    }
}
