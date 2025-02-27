package tasktracker.manager;

//Счетчик для генерации уникальных id для задач
class IdIterator {
    protected int counterID = 1;

    int generateId() {
        return counterID++;
    }

}
