package Socket;

import java.io.Serializable;

public class ToDoItem implements Serializable {
    private String task;
    private boolean done;

    public ToDoItem(String task, boolean done) {
        this.task = task;
        this.done = done;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
