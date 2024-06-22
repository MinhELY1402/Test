package Socket;
// MainController.java
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    private MainView mainView;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private User user;
    private Database database;

    public MainController(MainView mainView, Socket socket, ObjectOutputStream out, ObjectInputStream in, User user, Database database) {
        this.mainView = mainView;
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.user = user;
        this.database = database;

        // Tải nhiệm vụ từ cơ sở dữ liệu và hiển thị trong MainView
        loadToDoItems();

        new Thread(() -> {
            try {
                while (true) {
                    Object obj = in.readObject();
                    if (obj instanceof ToDoItem) {
                        mainView.addTask((ToDoItem) obj);
                    } else if (obj instanceof Message) {
                        mainView.displayMessage((Message) obj);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

        this.mainView.addTaskListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = JOptionPane.showInputDialog(mainView, "Enter task:");
                if (task != null && !task.trim().isEmpty()) {
                    ToDoItem item = new ToDoItem(task, false);
                    try {
                        database.addToDoItem(user, item);
                        out.writeObject(item);
                        out.flush();
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        this.mainView.addSendMessageListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = mainView.getChatInput();
                if (message != null && !message.trim().isEmpty()) {
                    try {
                        out.writeObject(new Message(user.getUsername(), message));
                        out.flush();
                        mainView.clearChatInput();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        this.mainView.addDoneButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToDoItem selectedTask = mainView.getSelectedTask();
                if (selectedTask != null) {
                    selectedTask.setDone(true);
                    try {
                        database.updateToDoItem(user, selectedTask);
                        out.writeObject(selectedTask);
                        out.flush();
                        mainView.updateTask(selectedTask);
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadToDoItems() {
        try {
            List<ToDoItem> items = database.getToDoItems(user);
            mainView.addTasks(items);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
