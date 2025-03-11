package tasktracker.tasks;

import tasktracker.enumeration.Status;
import tasktracker.enumeration.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIdList = new ArrayList<>();// Список id подзадач у эпика

    private LocalDateTime endTime;

    public Epic(String name, String description) {
        // Помещаем в конструктор супер класса значение времени и продолжения как null Т.к. Эпик, без подзадач.
        // Статус эпика по-умолчанию NEW
        super(name, description, Status.NEW, null, null);
        this.endTime = null;
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getStatus(), epic.getStartTime(), epic.getDuration());
        this.setId(epic.getId());
        this.setSubtaskIdList(epic.getSubtaskIdList());
        this.endTime = epic.getEndTime();

    }

    // переопределяем метод и получаем тип - Эпик
    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Создаем сеттер для получения поля endTime, внесения в него времени самой продолжительной подзадачи
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // Использую тернарный оператор для правильного вывода метода toString с форматированием
    @Override
    public String getEndTimeToString() {
        return (endTime != null) ? endTime.format(getFormatter()) : null;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIdList=" + subtaskIdList +
                ", startTime=" + getStartTimeToString() +
                ", duration=" + getDurationToString() +
                ", endTime=" + getEndTimeToString() +
                '}';
    }


    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(int id) {
        this.subtaskIdList.add(id);
    }

    public void setSubtaskIdList(ArrayList<Integer> subtaskIdList) {     // OVERLOAD метод для копирования epica в другой объект
        this.subtaskIdList.addAll(subtaskIdList);                        // Через getSubtaskIdlist() получим список Id
    }                                                                    // Закинем этот список через этот метод, для копирования.

}
