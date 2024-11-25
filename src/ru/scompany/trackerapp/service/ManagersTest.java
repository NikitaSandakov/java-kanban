package ru.scompany.trackerapp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ManagersTest {

    @Test
    public void testGetDefaultHistoryReturnsInitializedManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager should not be null");

        assertTrue(historyManager instanceof InMemoryHistoryManager, "Expected instance of InMemoryHistoryManager");

    }
}