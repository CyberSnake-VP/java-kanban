package tasktracker.manager;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();


    @Override
    public void add(Task task) {
        int limitHistory = 10;

        if (history.size() == limitHistory) {  // Проверка списка истории на лимит
            history.removeFirst();             // Удаляем первый элемент списка
        }

        history.add(task);     // Добавляем задачу в историю
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyCopy = new ArrayList<>();

        for (Task task : history) {       // Перебираем список истории, проверяем объект и добавляем в список
            if (task instanceof Epic) {
                historyCopy.add(new Epic((Epic) task));

            } else if (task instanceof Subtask) {
                Epic epicDefault = new Epic(null, null);
                epicDefault.setId(((Subtask) task).getEpicId());

                historyCopy.add(new Subtask((Subtask) task, epicDefault));

            } else {
                historyCopy.add(new Task(task));

            }
        }

        return historyCopy;      // Возвращаем список истории из копий объектов
    }                            // Отсутствует возможность изменять объекты в оригинальном списке истории.
}
