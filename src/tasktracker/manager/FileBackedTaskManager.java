package tasktracker.manager;

import tasktracker.exceptions.ManagerSaveException;
import tasktracker.tasks.*;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File data;

    public FileBackedTaskManager(File file) {
        this.data = file;
    }

    public void save() {
        String taskForWrite = getAllTasksToFile();
        try (Writer fw = new FileWriter(data)) {
            fw.write(taskForWrite);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл.");
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

}
