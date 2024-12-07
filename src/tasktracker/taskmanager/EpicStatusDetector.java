package tasktracker.taskmanager;

import tasktracker.status.Status;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import java.util.ArrayList;

class EpicStatusDetector {
    // Метод для обновления статуса у эпика
    public void setEpicStatus(Epic epic, ArrayList<Subtask> epicSubtasksList) {
        if(epicSubtasksList.isEmpty()){
            epic.setStatus(Status.NEW);
        }
        boolean isNew = false;
        for (Subtask sub : epicSubtasksList) {
            switch (sub.getStatus()) {
                case IN_PROGRESS:
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                case NEW:
                    isNew = true;
                    break;
            }
        }
        if(isNew) {
            epic.setStatus(Status.NEW);        // Если нет подзадач с IN_PROGRESS, но есть с NEW статус эпика будет NEW
        }else {
            epic.setStatus(Status.DONE);       // Тогда в эпике задачи со статусом DONE, делаем статус эпика DONE
        }
    }
}
