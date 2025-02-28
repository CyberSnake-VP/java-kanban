package tasktracker.manager;

import tasktracker.enumeration.Status;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.time.Duration;
import java.util.*;


class Identifier {

    /**
     * Утилитарный класс.
     * Определяет статус эпика с учетом его подзадач.
     * Определяет время начала, продолжительность, окончания выполнения
     */


    static void setEpicStatus(Epic epic, ArrayList<Subtask> epicSubtasksList) {
        if (epicSubtasksList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
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
        if (isNew) {
            epic.setStatus(Status.NEW);        // Если нет подзадач с IN_PROGRESS, но есть с NEW статус эпика будет NEW
        } else {
            epic.setStatus(Status.DONE);       // Тогда в эпике задачи со статусом DONE, делаем статус эпика DONE
        }
    }

    static void setEpicTime(Epic epic, List<Subtask> epicSubtaskList) {

        // Если у всех подзадач не указано время начала, устанавливаем null'ы и возвращаемся
        boolean isNullALl = epicSubtaskList.stream().allMatch(subtask -> subtask.getStartTime() == null);

        if (epicSubtaskList.isEmpty() || isNullALl) {
            epic.setStartTime(null);
            epic.setDuration(null);
            return;
        }

        // Стрим для установки Начального времени выполнения задачи.
        // Фильтруем поле startTime, чтобы не было null, после определяем минимальное время и записываем в Epic
        epicSubtaskList.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Task::getStartTime))
                .ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));

        epic.setDuration(Duration.ofMinutes(0));  // Обнуляем Duration перед новым подсчетом, и заодно решаем вопрос с возможным null

        // Стрим, решает вопрос сложения Duration всех подзадач
        epicSubtaskList.stream()
                .filter(subtask -> subtask.getDuration() != null)
                .forEach(subtask -> epic.setDuration(epic.getDuration().plus(subtask.getDuration())));

        // Стрим проверяет endTime у подзадач, находим самую позднюю во выполнению и устанавливаем в поле Epic'а EndTime
        epicSubtaskList.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(Task::getEndTime))
                .ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));
    }
}
