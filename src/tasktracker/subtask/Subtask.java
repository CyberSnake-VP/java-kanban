package tasktracker.subtask;

import tasktracker.epic.Epic;
import tasktracker.status.Status;
import tasktracker.task.Task;

public class Subtask extends Task {
     private int epicId;  // id конкретного эпика, для связывания этой подзадачи с объектом эпик, в рамках которой она создается

     public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        epicId = epic.getId();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return   "Subtask{" +
                    "name='" + getName() + '\'' +
                    ", description='" + getDescription() + '\'' +
                    ", id=" + getId() +
                    ", status=" + getStatus() + '\'' +
                    ", epicId=" + epicId +
                '}';

    }
}


