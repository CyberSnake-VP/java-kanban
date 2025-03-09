package tasktracker.tasks;

import tasktracker.enumeration.Status;
import tasktracker.enumeration.Type;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    // Конструктор с временем
    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = LocalDateTime.now();
        this.duration = duration;
    }

    public Task(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.id = task.getId();
        this.duration = task.getDuration();
        this.startTime = task.getStartTime();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    // Добавляем геттеры для получения полей duration и startTime
    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    // Методы для получения строки для вывода метода toString у всех задач
    // с форматированием и проверкой на null для избежания проблем с форматированием
    public String getStartTimeToString() {
        if (startTime != null) {
            return startTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm"));
        } else {
            return null;
        }
    }

    public String getDurationToString() {
        if (duration != null) {
            return duration.toMinutes() + "";
        } else {
            return null;
        }
    }

    public String getEndTimeToString() {
        if (getEndTime() != null) {
            return getEndTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm"));
        }
        return null;
    }

    // Чтобы не было проблем с подсчетом конечного времени, нужно проверить входные данные на null
    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    // Для удобного отображения времени, создал formatter и сделал его доступным для остальных классов.
    public DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm");
    }

    // Получаем тип задачи
    public Type getType() {
        return Type.TASK;
    }

    // Перезаписываем equals через поле id
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    // Перезаписываем hashCode через поле id
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + getStartTimeToString() +
                ", duration=" + getDurationToString() +
                ", endTime=" + getEndTimeToString() +
                '}';
    }
}
