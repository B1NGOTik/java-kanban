package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Task;

public class InMemoryHistoryManager implements HistoryManager {
    public static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    Node firstNode;
    Node lastNode;
    HashMap<Integer, Node> nodesById = new HashMap<>();

    public void linkLast(Node newNode) {
        if (firstNode == null) {
            firstNode = newNode;
        } else {
            lastNode.next = newNode;
            newNode.prev = lastNode;

        }
        lastNode = newNode;
        nodesById.put(newNode.task.getId(), newNode);
    }

    public void removeNode(Node node) {
        if (lastNode == node) {
            lastNode.prev.next = null;
            lastNode = lastNode.prev;
        } else if (firstNode == node) {
            firstNode.next.prev = null;
            firstNode = firstNode.next;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        nodesById.remove(node.task.getId());
    }

    public List<Node> getTasks() {
        List<Node> nodes = new ArrayList<>();
        Node element = firstNode;
        while (element != lastNode) {
            nodes.add(element);
            element = element.next;
        }
        nodes.add(element);
        return nodes;
    }

    @Override
    public void add(Task task) {
        if (nodesById.containsKey(task.getId())) {
            removeNode(nodesById.get(task.getId()));
        }
        Node node = new Node(task, null, null);
        linkLast(node);
    }

    @Override
    public void remove(int id) {
        Node node = nodesById.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> taskHistory = new ArrayList<>();
        List<Node> nodes = getTasks();
        for (Node node : nodes) {
            taskHistory.add(node.task);
        }
        return taskHistory;
    }

}
