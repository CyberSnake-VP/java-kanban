package tasktracker.manager;

import tasktracker.status.Status;
import tasktracker.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();                // Используем хеш таблицу для хранения задач
    private final HashMap<Integer, Epic> epics = new HashMap<>();                // Эпиков
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();          // Подзадач для эпиков

    private final IdIterator iteratorId = new IdIterator();                      // Подключаем генератор id
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

        return task;                                //Вернем пользователю задачу с заполненным полем id
    }

    @Override
    public boolean updateTask(Task task) {          // Обновление задачи, если задачи нет, то вернем false, т.е. не обновлена
        if (tasks.containsValue(task)) {
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
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);                               // Получаем id задачи из таблицы и удаляем задачу из истории
        }
        tasks.clear();                                               // Удаляем список задач. Возвращаем список удаленных задач
        return taskList;
    }

    @Override
    public Task deleteTask(int id) {
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
            StatusDetector.setEpicStatus(epic, epicSubtasksList);             // Обновляем статус эпика по его подзадачам

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
            for (Integer id : epics.get(epic.getId()).getSubtaskIdList()) {
                subtaskList.add(subtasks.get(id));                       // Если эпик есть, получаем список его список id подзадач
            }                                                            // Получаем подзадачу из таблицы по id из списка id подзадач эпика
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

        for (Epic epic : epics.values()) {                                 // Пробегаемся по эпиками, получаем список их подзадач
            for (Integer idSub : epic.getSubtaskIdList()) {
                historyManager.remove(idSub);                            // Удаляем все подзадачи из истории
            }
            historyManager.remove(epic.getId());                         // Удаляем все эпики из истории
        }

        epics.clear();                                                    // Очищаем список эпиков
        subtasks.clear();                                                 // Очищаем список подзадач
        return epicList;                                                  // Возвращаем список удаленных
    }

    @Override
    public Epic deleteEpic(int id) {                                      // Удаляем эпик по id
        if (epics.containsKey(id)) {
            final Epic epic = epics.get(id);
            for (Integer subId : epic.getSubtaskIdList()) {               // Получаем список id его подзадач
                historyManager.remove(subId);                             // Удаляем подзадачу из истории
                subtasks.remove(subId);                                   // Удаляем его подзадачи из таблицы подзадач
            }
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
        StatusDetector.setEpicStatus(epic, epicSubtasksList);             // метод setEpicStatus устанавливает статус эпика

        return subtask;     // Возвращаем объект подзадачи.
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {                          // Обновление подзадачи
        if (subtasks.containsValue(subtask)) {
            Epic epic = epics.get(subtask.getEpicId());
            subtasks.put(subtask.getId(), new Subtask(subtask, epic));       // Кладем копию подзадачи

            ArrayList<Subtask> epicSubtaskList = getSubtaskListInEpic(epic); // Получаем список подзадач эпика
            StatusDetector.setEpicStatus(epic, epicSubtaskList);             // Обновляем статус эпика
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
        for (Integer idSub : subtasks.keySet()) {
            historyManager.remove(idSub);                             // Удаляем все подзадачи из истории
        }
        subtasks.clear();                                             // Очищаем таблицу подзадач
        for (Epic epic : epics.values()) {                            // Пробегаемся по всем эпикам из таблицы
            epic.getSubtaskIdList().clear();                          // Очищаем списки id подзадач у эпиков
            epic.setStatus(Status.NEW);                               // Устанавливаем им статус NEW
        }
        return subtaskList;
    }

    @Override
    public ArrayList<Subtask> deleteSubtaskList(Epic epic) {             // Удаление подзадач у конкретного эпика
        if (epics.containsValue(epic)) {
            ArrayList<Subtask> subtask = getSubtaskListInEpic(epic);     // Получаем список подзадач у эпика
            for (Integer id : epic.getSubtaskIdList()) {
                historyManager.remove(id);                               // Удаляем подзадачи из истории
                subtasks.remove(id);                                     // Удаляем подзадачи у эпика из таблицы подзадач
            }
            epic.setStatus(Status.NEW);                                  // Обновляем статус по умолчанию NEW
            epic.getSubtaskIdList().clear();                             // Чистим список подзадач у эпика.

            return subtask;                                              // Возвращаем список удаленных подзадач
        }
        return null;
    }

    @Override
    public Subtask deleteSubtask(int id) {                               // Удаление подзадачи
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);                          // Получаем подзадачу из таблицы
            Epic epic = epics.get(subtask.getEpicId());                  // Получаем эпик этой подзадачи

            ArrayList<Integer> subtaskListId = epic.getSubtaskIdList();  // Получаем список id подзадач у эпика
            subtaskListId.remove((Integer) subtask.getId());             // Удаляем подзадачу по id из списка подзадач у эпика
            ArrayList<Subtask> epicSubtasksList = getSubtaskListInEpic(epic); // Получаем список объектов подзадач у эпика

            StatusDetector.setEpicStatus(epic, epicSubtasksList);    // Обновляем статус у эпика

            historyManager.remove(id);                               // Удаляем подзадачу из истории
            return subtasks.remove(subtask.getId());   // Удаляем задачу из таблицы и возвращаем объект удаленной задачи
        }

        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
