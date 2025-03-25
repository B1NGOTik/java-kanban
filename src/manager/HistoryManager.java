package manager;

import java.util.List;

import model.Task;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);

    void remove(int id);
}
