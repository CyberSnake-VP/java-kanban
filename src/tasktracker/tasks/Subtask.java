package tasktracker.tasks;

import tasktracker.enumeration.Status;
import tasktracker.enumeration.Type;

import java.time.*;

public class Subtask extends Task {
    private final int epicId;  // id конкретного эпика, для связывания этой подзадачи с объектом эпик, в рамках которой она создается

//    public Subtask(String name, String description, Epic epic, Status status) {
//        super(name, description, status);
//        epicId = epic.getId();
//    }
//
//    public Subtask(String name, String description, Epic epic) {
//        super(name, description, Status.NEW);
//        epicId = epic.getId();
//    }



    //------------------
    // НОВЫЙ КОНСТРУКТОР!
    public Subtask(String name, String description, Epic epic, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        epicId = epic.getId();
    }
    public Subtask(String name, String description, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        epicId = epic.getId();
    }

    public Subtask(String name, String description, Epic epic, Duration duration) {
        super(name, description, duration);
        epicId = epic.getId();
    }

    public Subtask(Subtask subtask, Epic epic) {
        super(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getStartTime(), subtask.getDuration());
        this.setId(subtask.getId());
        epicId = epic.getId();
    }

    //-----------

    public int getEpicId() {
        return epicId;
    }

    // переопределяем метод для получения типа подзадача
    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", startTime=" + getStartTimeToString() +
                ", duration=" + getDurationToString() +
                ", endTime=" + getEndTimeToString()+
                '}';

    }
}


