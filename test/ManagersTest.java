package test;

import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;
import ru.scompany.trackerapp.service.Managers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    public void testGetDefaultHistoryReturnsInitializedManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager не должен быть нулевым");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);

    }

}