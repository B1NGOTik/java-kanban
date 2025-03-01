import manager.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void getDefaultShouldInitializeInMemoryTaskManager() {
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    public void getDefaultHistoryShouldInitializeInMemoryHistoryManager() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }
}
