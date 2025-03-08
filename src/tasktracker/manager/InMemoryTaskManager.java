package tasktracker.manager;

import tasktracker.enumeration.Status;
import tasktracker.exceptions.ManagerValidationIsFailed;
import tasktracker.tasks.*;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();                // Используем хеш таблицу для хранения задач
    protected final HashMap<Integer, Epic> epics = new HashMap<>();                // Эпиков
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();          // Подзадач для эпиков
    protected final Set<Task> prioritizedTask = new TreeSet<>(Comparator.comparing(Task::getStartTime)); // Задачи по приоритету

    protected final IdIterator iteratorId = new IdIterator();                      // Подключаем генератор id
    private final HistoryManager historyManager = Managers.getDefaultHistory();  // Подключаем HistoryManager


    // Методы для Task
    @Override
    public Task createTask(Task task) {             // Создание задачи
        if (tasks.containsValue(task)) {            // Проверяем на наличие задачи в списке задач,
            return null;                            // Если задача уже существует, не создаем ее. Вернем null
        }
        if (task == null) {                          // Проверяем существует ли задача.
            return null;
        }
        int id = iteratorId.generateId();           // Генерируем уникальный id
        task.setId(id);                             // Запись id в поле задачи.
        tasks.put(task.getId(), new Task(task));    // Кладем в таблицу копию задачи
        addTaskInPriority(new Task(task));                    // Кладем задачу в treeSet для сортировки приоритета по timeStart'у
        return task;                                //Вернем пользователю задачу с заполненным полем id
    }

    /**
     * Копия задач в приоритет кладется, чтобы с помощью setStartTime() не изменить вручную время, чтобы не сломать логику
     */
    @Override
    public boolean updateTask(Task task) {          // Обновление задачи, если задачи нет, то вернем false, т.е. не обновлена
        if (tasks.containsValue(task)) {
            prioritizedTask.remove(tasks.get(task.getId()));  // Удаляем задачу из приоритета
            addTaskInPriority(new Task(task));              // Кладем обновленную задачу в приоритет
            tasks.put(task.getId(), new Task(task)); // Записываем копию задачи в таблицу, возвращаем true;
            return true;
        }
        return false;
    }

    //Получаем список задач, в конструктор ArrayList(положим коллекцию, которую вернет метод Values())
    @Override
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTask(int id) {                // Получение задачи по id
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            //Я думал, что запись копии задачи в историю просмотров необходима, чтобы при изменении задачи, методом updateTask
            //не менялась версия задачи в списке истории, тем самым достигаем сохранения предыдущей версии задачи в истории по ТЗ
            historyManager.add(new Task(task));
            return new Task(task);                         // Возвращаем копию задачи
        }
        return null;
    }

    @Override
    public ArrayList<Task> deleteTaskList() {                        // Удаление списка всех задач
        ArrayList<Task> taskList = new ArrayList<>(tasks.values());
        tasks.forEach((id, task) -> {
            historyManager.remove(id);                               // Очищаем история просмотров
            prioritizedTask.remove(task);                            // Удаляем задачи из списка приоритета
        });
        tasks.clear();                                               // Удаляем список задач. Возвращаем список удаленных задач
        return taskList;
    }

    @Override
    public Task deleteTask(int id) {
        prioritizedTask.remove(tasks.get(id));                        // Удаляем задачу из списка приоритета
        historyManager.remove(id);                                  // Удаляем задачу из истории
        return tasks.remove(id);                                    // Удаляем задачу по id из таблицы
    }


    // Методы для Epic
    @Override
    public Epic createEpic(Epic epic) {                            // Создаем эпик(Глобальную задачу)
        if (epics.containsValue(epic)) {
            return null;
        }
        if (epic == null) {                                         // Проверяем существует ли эпик.
            return null;
        }
        int id = iteratorId.generateId();                          // Генерируем id из записываем id в поле эпика
        epic.setId(id);
        epics.put(epic.getId(), new Epic(epic));                   // Записываем копию эпика в таблицу, возвращаем эпик с id
        return epic;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            ArrayList<Subtask> epicSubtasksList = getSubtaskListInEpic(epic); // Получаем подзадачи эпика
            Identifier.setEpicStatus(epic, epicSubtasksList);                 // Обновляем статус эпика по его подзадачам
            Identifier.setEpicTime(epic, getSubtaskListInEpic(epic));         // Обновляем время выполнения у эпика
            epics.put(epic.getId(), new Epic(epic));                          // Записываем копию в таблицу с эпиками
            return true;                                                      // Обновление успешно
        }
        return false;
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());         // Список эпиков
    }

    @Override
    public ArrayList<Subtask> getSubtaskListInEpic(Epic epic) {          // Получение списка подзадач у эпика
        if (epics.containsValue(epic)) {
            ArrayList<Subtask> subtaskList = new ArrayList<>();
            // Если эпик есть, получаем список его список id подзадач
            // Получаем подзадачу из таблицы по id из списка id подзадач эпика
            epics.get(epic.getId()).getSubtaskIdList().forEach(id -> subtaskList.add(subtasks.get(id)));
            return subtaskList;
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {                                        // Получаем эпик по id
        if (epics.containsKey(id)) {
            final Epic epic = epics.get(id);                             // Получаем эпик из таблицы, создаем его копию
            historyManager.add(new Epic(epic));                          // Запись копии эпика в историю просмотров
            return new Epic(epic);                                       // Возвращаем копию эпика
        }
        return null;
    }

    @Override
    public ArrayList<Epic> deleteEpicList() {                             // Удаляем список всех эпиков
        ArrayList<Epic> epicList = new ArrayList<>(epics.values());

        epics.values().forEach(epic -> {
                    epic.getSubtaskIdList().forEach(idSub -> {   // Пробегаемся по эпиками, получаем список их подзадач
                        prioritizedTask.remove(subtasks.get(idSub));       // Удаляем все подзадачи из списка приоритета
                        historyManager.remove(idSub);                    // Удаляем все подзадачи из истории
                    });
                    historyManager.remove(epic.getId());                 // Удаляем все эпики из истории
                    epic.setStartTime(null);
                }
        );
        epics.clear();                                                    // Очищаем список эпиков
        subtasks.clear();                                                 // Очищаем список подзадач
        return epicList;                                                  // Возвращаем список удаленных
    }

    @Override
    public Epic deleteEpic(int id) {                                      // Удаляем эпик по id
        if (epics.containsKey(id)) {
            final Epic epic = epics.get(id);
            epic.getSubtaskIdList().forEach(subId -> {            // Получаем список id его подзадач
                prioritizedTask.remove(subtasks.get(subId));                // Удаляем все подзадачи из списка приоритета
                historyManager.remove(subId);                             // Удаляем подзадачу из истории
                subtasks.remove(subId);                                   // Удаляем его подзадачи из таблицы подзадач
            });

            historyManager.remove(id);                                    // Удаляем эпик из истории
            return epics.remove(id);                                      // Удаляем эпик из таблицы и возвращаем удаленный эпик
        }
        return null;
    }


    // Методы для Subtask
    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask)) {            // Проверка, есть ли уже такая подзадача.
            return null;
        }
        if (subtask == null) {                             // Проверяем существует ли подзадача.
            return null;
        }
        int id = iteratorId.generateId();                 // Генерация ID
        subtask.setId(id);                                // Запись в поле id подзадачи(subtask)
        Epic epic = epics.get(subtask.getEpicId());       // Получение Эпика, из таблицы, к которому привяжем подзадачу.
        epic.setSubtaskIdList(subtask.getId());
        subtasks.put(subtask.getId(), new Subtask(subtask, epic));        // Записываем копию в список подзадач
        ArrayList<Subtask> epicSubtasksList = getSubtaskListInEpic(epic); // Получаем список подзадач у Эпика
        Identifier.setEpicStatus(epic, epicSubtasksList);             // метод setEpicStatus устанавливает статус эпика
        Identifier.setEpicTime(epic, epicSubtasksList);               // Устанавливаем необходимое время выполнения
        addTaskInPriority(new Subtask(subtask, epic));                                   // Кладем подзадачу в treeSet
        return subtask;     // Возвращаем объект подзадачи.
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {                          // Обновление подзадачи
        if (subtasks.containsValue(subtask)) {
            Epic epic = epics.get(subtask.getEpicId());
            prioritizedTask.remove(subtasks.get(subtask.getId()));             // удаляем подзадачу из приоритета
            addTaskInPriority(new Subtask(subtask, epic));                   // Добавляем обновленную подзадачу в приоритет
            subtasks.put(subtask.getId(), new Subtask(subtask, epic));       // Кладем копию подзадачи

            ArrayList<Subtask> epicSubtaskList = getSubtaskListInEpic(epic); // Получаем список подзадач эпика
            Identifier.setEpicStatus(epic, epicSubtaskList);                 // Обновляем статус эпика
            Identifier.setEpicTime(epic, epicSubtaskList);                   // Время выполнения  по подзадачам
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {    // Получение списка всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtask(int id) {                             // Получение подзадачи по id
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            historyManager.add(new Subtask(subtask, epic));         // Запись копии подзадачи в историю просмотров
            return new Subtask(subtask, epic);                      // Возврат копии подзадачи
        }
        return null;
    }

    @Override
    public ArrayList<Subtask> deleteSubtaskList() {                    // Удалить список всех подзадач
        ArrayList<Subtask> subtaskList = new ArrayList<>(subtasks.values());

        subtasks.keySet().forEach(idSub -> {                   // Удаляем задачу из списка приоритета
            prioritizedTask.remove(subtasks.get(idSub));                 // Удаляем все подзадачи из истории
            historyManager.remove(idSub);
        });
        subtasks.clear();                                              // Очищаем таблицу подзадач

        epics.values().forEach(epic -> {                          // Пробегаемся по всем эпикам из таблицы
            epic.getSubtaskIdList().clear();                           // Очищаем списки id подзадач у эпиков
            epic.setStatus(Status.NEW);                                // Устанавливаем им статус NEW
            Identifier.setEpicTime(epic, getSubtaskListInEpic(epic));
        });

        return subtaskList;
    }

    @Override
    public ArrayList<Subtask> deleteSubtaskList(Epic epic) {             // Удаление подзадач у конкретного эпика
        if (epics.containsValue(epic)) {
            ArrayList<Subtask> subtask = getSubtaskListInEpic(epic);     // Получаем список подзадач у эпика

            epic.getSubtaskIdList().forEach(id -> {                 // Удаляем подзадачи из списка приоритета
                prioritizedTask.remove(subtasks.get(id));                     // Удаляем подзадачи из истории
                historyManager.remove(id);                                  // Удаляем подзадачи у эпика из таблицы подзадач
                subtasks.remove(id);
            });

            Epic epicFromMap = epics.get(epic.getId());                  // Получаем объект эпика из мапы и меняем зн-е полей
            epicFromMap.setStatus(Status.NEW);                                  // Обновляем статус по умолчанию NEW
            epicFromMap.getSubtaskIdList().clear();                             // Чистим список подзадач у эпика.
            Identifier.setEpicTime(epicFromMap, getSubtaskListInEpic(epicFromMap)); // Сбрасываем время выполнения в null

            return subtask;                                              // Возвращаем список удаленных подзадач
        }
        return null;
    }

    @Override
    public Subtask deleteSubtask(int id) {                               // Удаление подзадачи
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);                          // Получаем подзадачу из таблицы
            Epic epic = epics.get(subtask.getEpicId());                  // Получаем эпик этой подзадачи

            prioritizedTask.remove(subtasks.get(id));                      // Удаляем подзадачу из приоритета
            ArrayList<Integer> subtaskListId = epic.getSubtaskIdList();  // Получаем список id подзадач у эпика
            subtaskListId.remove((Integer) subtask.getId());             // Удаляем подзадачу по id из списка подзадач у эпика
            ArrayList<Subtask> epicSubtasksList = getSubtaskListInEpic(epic); // Получаем список объектов подзадач у эпика

            Identifier.setEpicStatus(epic, epicSubtasksList);    // Обновляем статус у эпика
            Identifier.setEpicTime(epic, epicSubtasksList);
            historyManager.remove(id);                               // Удаляем подзадачу из истории
            return subtasks.remove(subtask.getId());   // Удаляем задачу из таблицы и возвращаем объект удаленной задачи
        }

        return null;
    }

    /**
     * Добавляем задачу в список приоритета, с указанием пользователю, если проверка на валидность не пройдена
     */
    protected void addTaskInPriority(Task task) {
        try {
            if ((task.getStartTime() != null && task.getDuration() != null) && validateTask(task)) {
                prioritizedTask.add(task);
            } else {
                throw new ManagerValidationIsFailed("Опс.. Задача не попала в список приоритета.");
            }
        } catch (ManagerValidationIsFailed e) {
            System.out.println(e.getMessage() + " " + task.getName() + "\nОбновите задачу с указанием правильного времени, для появлении ее в списке по приоритету.");

        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Получение списка приоритетных задач
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTask);
    }

    // Метод для проверки добавляемой задачи на валидацию по времени начала и окончания выполнения
    private boolean validateTask(Task newTask) {
        if (getPrioritizedTasks().isEmpty()) {
            return true;
        }
        return getPrioritizedTasks().stream()
                .allMatch(task -> newTask.getStartTime().isBefore(task.getStartTime()) &&
                        (newTask.getEndTime().isBefore(task.getStartTime()) || newTask.getEndTime().equals(task.getStartTime()))
                        || (newTask.getStartTime().isAfter(task.getEndTime()) || newTask.getStartTime().equals(task.getEndTime()))
                        && newTask.getEndTime().isAfter(task.getEndTime()));
        /**
         Идея стрима в том, чтобы пройтись по всем задачам и проверить на условие, что добавляемая задача,
         по времени начала раньше, чем начало выполнения задачи в списке и время окончания задачи раньше или равное времени
         начала задачи в списке. И наоборот, начало добавляемой задачи позже или равное окончанию выполнения задачи в списке,
         а так же время окончания задачи позже окончания выполнения задачи из списка.
         */
    }
}
