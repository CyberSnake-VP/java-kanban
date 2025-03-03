package tasktracker.exceptions;

import tasktracker.tasks.Task;

public class ManagerValidationIsFaled extends Exception {
    public ManagerValidationIsFaled(String message) {
        super(message);
    }
}