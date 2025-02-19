package tasktracker.manager;

import tasktracker.exceptions.ManagerSaveException;
import tasktracker.status.Status;
import tasktracker.tasks.*;

import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File data;

    public FileBackedTaskManager(File file) {
        this.data = file;
    }

    public void save() {
        String taskForWrite = getStringTasks();
        try (Writer fw = new FileWriter(data)) {
            fw.write(taskForWrite);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл.");
        }

    }

    private String getStringTasks() {
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

    public String toString(Task task) {
        String type = TypeTasks.TASK.name();
        String id = Integer.toString(task.getId());
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

}
