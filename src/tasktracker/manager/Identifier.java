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

        /** если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
         если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
         во всех остальных случаях статус должен быть IN_PROGRESS. */

        if (epicSubtasksList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        // Все подзадачи со статусом NEW эпик должен иметь статус NEW
        boolean isAllSubtaskStatusNEW = epicSubtasksList.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW);
        // Все подзадачи со статусом DONE эпик должен иметь статус DONE
        boolean isAllSubtaskStatusDONE = epicSubtasksList.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        if (isAllSubtaskStatusNEW) {
            epic.setStatus(Status.NEW);
            return;
        }
        if(isAllSubtaskStatusDONE) {
            epic.setStatus(Status.DONE);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);

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

        // Обнуляем Duration перед новым подсчетом, и заодно решаем вопрос с возможным null

        // Стрим, решает вопрос сложения Duration всех подзадач
        Duration sum = epicSubtaskList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(sum);
        /** Reduce считает сумму с указанного начального значения identity, с элементами стрима значениями Duration.
         *  identity, (duration, duration2)-> duration.plus(duration2) */

        // Стрим проверяет endTime у подзадач, находим самую позднюю во выполнению и устанавливаем в поле Epic'а EndTime
        epicSubtaskList.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(Task::getEndTime))
                .ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));
    }
}
/**
 * Duration sum = epicSubtasks.stream()
 * .map(subtask::getDuration)
 * .filter(Objects::nonNull)
 * .reduce(Duration.ZERO, Duration::plus);
 */
