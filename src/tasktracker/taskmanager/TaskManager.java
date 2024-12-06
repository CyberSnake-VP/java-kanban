package tasktracker.taskmanager;
import tasktracker.status.Status;
import tasktracker.subtask.Subtask;
import tasktracker.task.Task;
import tasktracker.epic.Epic;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private  final HashMap <Integer, Task> tasks = new HashMap<>(); // Используем хеш таблицу для хранения задач
    private  final HashMap <Integer, Epic> epics = new HashMap<>();  // Эпиков
    private  final HashMap <Integer, Subtask> subtasks = new HashMap<>(); // Подзадач для эпиков

    IdIterator iteratorId = new IdIterator(); // Подключаем генератор id

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

            // Для изменения статус у Эпика, проверим статус на правильность, по его подзадачам.
            ArrayList<Subtask> epicSubtasksList = getSubtaskListInEpic(epicCopy); // Получаем подзадачи эпика
            if(epicSubtasksList.isEmpty()){
                epics.put(epicCopy.getId(), epicCopy); // Если его список подзадач пуст, тогда при создании объекта Копии
                return true;                           // эпика, по-умолчанию статус эпика будет NEW
            }
            boolean isInProgress = false;              // Если у эпика есть подзадачи, проверяем статус подзадач
            boolean isNew = false;
            for (Subtask subtask : epicSubtasksList) {
                switch (subtask.getStatus()) {
                    case IN_PROGRESS:                 // Есть ли в Эпике подзадачи со статусом IN_PROGRESS
                        isInProgress = true;
                        break;
                    case NEW:
                        isNew = true;                 // Есть ли в Эпике подзадачи со статусом NEW
                        break;
                }
            }
            if(isInProgress) {
                epicCopy.setStatus(Status.IN_PROGRESS);   // Есть подзадачи IN_PROGRESS значит эпик должен быть IN_PROGRESS
            }else if(isNew) {
                epicCopy.setStatus(Status.NEW);           // Если нет подзадач со статусом IN_PROGRESS, но есть со статусом NEW
            }else {
                epicCopy.setStatus(Status.DONE);          // В эпике нет подзадач IN_PROGRESS и NEW, ставим статус DONE
            }
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
        Subtask subtaskCopy = new Subtask(subtask.getName(), subtask.getDescription(),subtask.getStatus(),epic);
        subtaskCopy.setId(id);                        // Создаем копию подзадачи с генерированным id
        epic.setSubtaskIdList(subtaskCopy.getId());
        subtasks.put(subtaskCopy.getId(), subtaskCopy); // Записываем копию подзадачи в список подзадач

        ArrayList<Subtask> epicSubtasksList = getSubtaskListInEpic(epic);  // Обновляем статус у Эпика
        boolean isInProgress = false;
        boolean isNew = false;
        for (Subtask sub : epicSubtasksList) {
            switch (sub.getStatus()) {
                case IN_PROGRESS:
                    isInProgress = true;
                    break;
                case NEW:
                    isNew = true;
                    break;
            }
        }
        if(isInProgress){
            epic.setStatus(Status.IN_PROGRESS);          // Если есть подзадача с IN_PROGRESS делаем статус эпика таким же
        }else if(isNew) {
            epic.setStatus(Status.NEW);                  // Если нет подзадач с IN_PROGRESS, но есть с NEW статус эпика будет NEW
        }else {
            epic.setStatus(Status.DONE);                 // Тогда в эпике задачи со статусом DONE, делаем статус эпика DONE
        }
        return subtask;   // Возвращаем объект подзадачи.
    }

    public boolean updateSubtask(Subtask subtask) {      // Обновление подзадачи
        if(subtasks.containsValue(subtask)) {
            Epic epic = epics.get(subtask.getEpicId());
            Subtask subtaskCopy = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), epic);
            subtaskCopy.setId(subtask.getId());
            subtasks.put(subtaskCopy.getId(), subtaskCopy);   // Аналогично с добавлением новой подзадачи, кладем копию подзадачи

            ArrayList<Subtask> epicSubtask = getSubtaskListInEpic(epic);    // Меняем статус эпика
            boolean isInProgress = false;
            boolean isNew = false;
            for (Subtask sub : epicSubtask) {
                switch (sub.getStatus()) {
                    case IN_PROGRESS:
                        isInProgress = true;
                        break;
                    case NEW:
                        isNew = true;
                        break;
                }
            }
            if(isInProgress){
                epic.setStatus(Status.IN_PROGRESS);
            }else if(isNew) {
                epic.setStatus(Status.NEW);
            }else {
                epic.setStatus(Status.DONE);
            }
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
            ArrayList<Subtask> subtasksList= getSubtaskListInEpic(epic); // Получаем список объектов подзадач у эпика
            if(subtasksList.isEmpty()){                                  // Обновляем статус у эпика по оставшимся подзадачам
                epic.setStatus(Status.NEW);
                return subtasks.remove(subtask.getId());                 // Если подзадач больше нет, то статус NEW
            }
            boolean isInProgress = false;                                // Обновляем статус эпика
            boolean isNew = false;
            for (Subtask sub: subtasksList) {
                switch (sub.getStatus()) {
                    case IN_PROGRESS:
                        isInProgress = true;
                        break;
                    case NEW:
                        isNew = true;
                        break;
                }
            }
            if(isInProgress) {
                epic.setStatus(Status.IN_PROGRESS);
            } else if (isNew) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.DONE);
            }
            return subtasks.remove(subtask.getId());   // Удаляем задачу из таблицы и возвращаем объект удаленной задачи
        }

        return null;
    }

}
