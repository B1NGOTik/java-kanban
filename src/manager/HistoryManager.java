package manager;

import java.util.List;

import model.Task;

public interface HistoryManager {
    List<Task> getHistory();
    void addHistory(Task task);
}
