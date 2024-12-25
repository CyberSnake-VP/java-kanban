package tasktracker.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdIteratorTest {

    private final IdIterator iterator = new IdIterator();

    @Test
    void generateId() {
        int firstId = iterator.generateId();
        int secondId = iterator.generateId();

        assertEquals(1, firstId);
        assertEquals(2, secondId);

    }
}