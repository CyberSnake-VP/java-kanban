package tasktracker.tasks;

import tasktracker.enumeration.Status;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIdList = new ArrayList<>();   // Список id подзадач у эпика

    public Epic(String name, String description) {
        super(name, description, Status.NEW);                             // Статус эпика по-умолчанию NEW
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getStatus());
        this.setId(epic.getId());
        this.setSubtaskIdList(epic.getSubtaskIdList());
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIdList=" + subtaskIdList +
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
