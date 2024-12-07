package tasktracker.taskmanager;

import tasktracker.status.Status;
import tasktracker.tasks.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private  final HashMap <Integer, Task> tasks = new HashMap<>(); // Используем хеш таблицу для хранения задач
    private  final HashMap <Integer, Epic> epics = new HashMap<>();  // Эпиков
    private  final HashMap <Integer, Subtask> subtasks = new HashMap<>(); // Подзадач для эпиков

    private final IdIterator iteratorId = new IdIterator(); // Подключаем генератор id
    private final EpicStatusDetector epicStatusDetector = new EpicStatusDetector(); // Подключаем определитель статус эпика

    // Методы для Task
    public Task createTask(Task task) {          // Создание задачи
        if(tasks.containsValue(task)){           // Проверяем на наличие задачи в списке задач,
            return null;                         // Если задача уже существует, не создаем ее. Вернем null
        }
        int id = iteratorId.generateId();       // Генерируем уникальный id
        task.setId(id);                         // Запись id в поле задачи.

        Task taskCopy = new Task(task.getName(),task.getDescription(), task.getStatus());
        taskCopy.setId(id);
        tasks.put(taskCopy.getId(), taskCopy);      // Записываем в таблицу задач копию задачи, она будет иметь другой адрес в памяти
                                                    // чтобы не дать пользователю возможность менять поля у задачи в не рамках методов
        return task;     //Вернем пользователя задачу с заполненным полем id
    }

    public boolean updateTask(Task task) {         // Обновление задачи, если задачи нет, то вернем false, т.е. не обновлена
        if(tasks.containsValue(task)) {
             Task taskCopy = new Task(task.getName(),task.getDescription(), task.getStatus());
             taskCopy.setId(task.getId());
             tasks.put(taskCopy.getId(), taskCopy); // Создаем копию задачи, записываем копию в таблицу, возвращаем true;
             return true;
        }
        return false;
    }
    //Получаем список задач, в конструктор ArrayList(положим коллекцию, которую вернет метод Values())
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(int id) {      // Получение задачи по id
        if(tasks.containsKey(id)) {
            Task task = tasks.get(id);     // Получаем задачу из таблицы по id
            Task taskCopy = new Task(task.getName(), task.getDescription(),task.getStatus());
            task.setId(task.getId());
            return taskCopy;                // Возвращаем копию задачи
        }
        return null;
    }

    public ArrayList<Task> deleteTaskList() {                        // Удаление списка всех задач
        ArrayList <Task> taskList = new ArrayList<>(tasks.values());
        tasks.clear();                                               // Удаляем список задач. Возвращаем список удаленных задач
        return taskList;
    }

    public Task deleteTask(int id) {
        return tasks.remove(id);                                    // Удаляем задачу по id из таблицы
    }


    // Методы для Epic
    public Epic createEpic(Epic epic) {                            // Создаем эпик(Глобальную задачу)
        if(epics.containsValue(epic)){
            return null;
        }

        int id = iteratorId.generateId();                         // Генерируем id из записываем id в поле эпика
        epic.setId(id);

        Epic epicCopy = new Epic(epic.getName(),epic.getDescription());
        epicCopy.setId(id);
        epics.put(epicCopy.getId(), epicCopy);                   // Записываем копию эпика в таблицу, возвращаем эпик с id

        return epic;
    }

    public boolean updateEpic(Epic epic) {
        if(epics.containsValue(epic)) {
            // Создаем копию, которую будем отправлять в таблицу epics.
            Epic epicCopy = new Epic(epic.getName(), epic.getDescription());
            epicCopy.setId(epic.getId());
            epicCopy.setSubtaskIdList(epic.getSubtaskIdList());       // Копируем список id подклассов(subtasks)
            ArrayList<Subtask> epicSubtasksList = getSubtaskListInEpic(epicCopy); // Получаем подзадачи эпика
            epicStatusDetector.setEpicStatus(epicCopy, epicSubtasksList);
            epics.put(epicCopy.getId(), epicCopy);        // Записываем копию эпика в таблицу с эпиками
            return true;                                  // Обновление успешно
        }
        return false;
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());         // Список эпиков
    }

    public ArrayList<Subtask> getSubtaskListInEpic(Epic epic) {          // Получение списка подзадач у эпика
        if(epics.containsValue(epic)) {
            ArrayList<Subtask> subtaskList = new ArrayList<>();
            for (Integer id : epics.get(epic.getId()).getSubtaskIdList()) {
                subtaskList.add(subtasks.get(id));                       // Если эпик есть, получаем список его список id подзадач
            }                                                            // Получаем подзадачу из таблицы по id из списка id подзадач эпика
            return subtaskList;
        }
        return null;
    }

    public Epic getEpicById(int id) {                                     // Получаем эпик по id
        if(epics.containsKey(id)){
            Epic epic = epics.get(id);                                    // Получаем эпик из таблицы, создаем его копию
            Epic epicCopy = new Epic(epic.getName(),epic.getDescription());
            epicCopy.setId(epic.getId());
            epicCopy.setSubtaskIdList(epic.getSubtaskIdList());           // Получаем список его id, и записываем его в список id у копии
            return epicCopy;                                              // Возвращаем копию
        }
        return null;
    }

    public ArrayList<Epic> deleteEpicList() {                             // Удаляем список всех эпиков
        ArrayList <Epic> epicList = new ArrayList<>(epics.values());
        epics.clear();                                                    // Очищаем список эпиков
        subtasks.clear();                                                 // Очищаем список подзадач
        return epicList;                                                  // Возвращаем список удаленных
    }

    public Epic deleteEpic(int id) {                                      // Удаляем эпик по id
        if(epics.containsKey(id)){
            Epic epic = epics.get(id);
            for (Integer subId : epic.getSubtaskIdList()) {                // Получаем список id его подзадач
                subtasks.remove(subId);                                   // Удаляем его подзадачи из таблицы подзадач
            }
            return epics.remove(id);                                      // Удаляем эпик из таблицы и возвращаем удаленный эпик
        }
            return null;
    }


    // Методы для Subtask
    public Subtask createSubtask (Subtask subtask) {
        if(subtasks.containsValue(subtask)){        // Проверка, есть ли уже такая подзадача.
            return null;
        }
        int id = iteratorId.generateId();            // Генерация ID
        subtask.setId(id);                           // Запись в поле id подзадачи(subtask)
        Epic epic = epics.get(subtask.getEpicId());  // Получение Эпика, из таблицы, к которому привяжем подзадачу.
        epic.setSubtaskIdList(subtask.getId());
        Subtask subtaskCopy = new Subtask(subtask.getName(), subtask.getDescription(),subtask.getStatus(),epic);
        subtaskCopy.setId(id);                        // Создаем копию подзадачи с генерированным id

        subtasks.put(subtaskCopy.getId(), subtaskCopy); // Записываем копию подзадачи в список подзадач
        ArrayList<Subtask> epicSubtasksList = getSubtaskListInEpic(epic);  // Получаем список подзадач у Эпика
        epicStatusDetector.setEpicStatus(epic, epicSubtasksList); // РЕФАКТОРИНГ, метод setEpicStatus устанавливает статус эпика
        return subtask;   // Возвращаем объект подзадачи.
    }

    public boolean updateSubtask(Subtask subtask) {      // Обновление подзадачи
        if(subtasks.containsValue(subtask)) {
            Epic epic = epics.get(subtask.getEpicId());
            Subtask subtaskCopy = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), epic);
            subtaskCopy.setId(subtask.getId());
            subtasks.put(subtaskCopy.getId(), subtaskCopy);   // Аналогично с добавлением новой подзадачи, кладем копию подзадачи

            ArrayList<Subtask> epicSubtaskList = getSubtaskListInEpic(epic); // Получаем список подзадач эпика
            epicStatusDetector.setEpicStatus(epic, epicSubtaskList);  // Обновляем статус эпика
            return true;
        }
        return false;
    }

    public ArrayList<Subtask> getSubtaskList() {    // Получение списка всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtaskById(int id) {         // Получение подзадачи по id
        if(subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            return new Subtask(subtask.getName(), subtask.getDescription(),subtask.getStatus(),epic); // Возврат копии подзадачи
        }
        return null;
    }

    public ArrayList<Subtask> deleteSubtaskList() {                    // Удалить список всех подзадач
        ArrayList <Subtask> subtaskList = new ArrayList<>(subtasks.values());
        subtasks.clear();                                             // Очищаем таблицу подзадач
        for (Epic epic : epics.values()) {                            // Пробегаемся по всем эпикам из таблицы
            epic.getSubtaskIdList().clear();                          // Очищаем списки id подзадач у эпиков
            epic.setStatus(Status.NEW);                               // Устанавливаем им статус NEW
        }
        return subtaskList;
    }

    public ArrayList<Subtask> deleteSubtaskList(Epic epic) {             // Удаление подзадач у конкретного эпика
        if(epics.containsValue(epic)){
            Epic epicCopy = epics.get(epic.getId());
            ArrayList<Subtask> subtask = getSubtaskListInEpic(epicCopy); // Получаем список подзадач у эпика
            for (Integer id : epicCopy.getSubtaskIdList()) {
                subtasks.remove(id);                                     // Удаляем подзадачи у эпика из таблицы подзадач
            }
            epicCopy.setStatus(Status.NEW);                              // Обновляем статус по умолчанию NEW
            epicCopy.getSubtaskIdList().clear();                         // Чистим список подзадач у эпика.

            return subtask;                                              // Возвращаем список удаленных подзадач
        }
        return null;
    }

    public Subtask deleteSubtask(int id) {                               // Удаление подзадачи
        if(subtasks.containsKey(id)){
            Subtask subtask = subtasks.get(id);                          // Получаем подзадачу из таблицы
            Epic epic = epics.get(subtask.getEpicId());                  // Получаем эпик этой подзадачи
            ArrayList<Integer> subtaskListId = epic.getSubtaskIdList();  // Получаем список id подзадач у эпика
            subtaskListId.remove((Integer) subtask.getId());             // Удаляем подзадачу по id из списка подзадач у эпика
            ArrayList<Subtask> epicSubtasksList= getSubtaskListInEpic(epic); // Получаем список объектов подзадач у эпика
            epicStatusDetector.setEpicStatus(epic, epicSubtasksList);    // Обновляем статус у эпика
            return subtasks.remove(subtask.getId());   // Удаляем задачу из таблицы и возвращаем объект удаленной задачи
        }

        return null;
    }

}
