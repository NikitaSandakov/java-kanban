package ru.scompany.trackerapp.test;

import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;
import ru.scompany.trackerapp.service.Managers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {

    @Test
    public void testGetDefaultHistoryReturnsInitializedManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager should not be null");

        assertTrue(historyManager instanceof InMemoryHistoryManager, "Expected instance of InMemoryHistoryManager");

    }
}
