package Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class MainView extends JFrame {
    private DefaultListModel<ToDoItem> toDoListModel;
    private JList<ToDoItem> toDoList;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton addTaskButton;
    private JButton sendMessageButton;
    private JButton doneButton;

    public MainView() {
        setTitle("ToDo List and Chat");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        toDoListModel = new DefaultListModel<>();
        toDoList = new JList<>(toDoListModel);
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatInput = new JTextField(20);
        addTaskButton = new JButton("Add Task");
        sendMessageButton = new JButton("Send");
        doneButton = new JButton("Done");

        JPanel panel = new JPanel(new BorderLayout());
        JPanel toDoPanel = new JPanel(new BorderLayout());
        JPanel chatPanel = new JPanel(new BorderLayout());

        toDoPanel.add(new JScrollPane(toDoList), BorderLayout.CENTER);

        JPanel toDoButtonPanel = new JPanel();
        toDoButtonPanel.add(addTaskButton);
        toDoButtonPanel.add(doneButton);
        toDoPanel.add(toDoButtonPanel, BorderLayout.SOUTH);

        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel chatInputPanel = new JPanel();
        chatInputPanel.add(chatInput);
        chatInputPanel.add(sendMessageButton);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        panel.add(toDoPanel, BorderLayout.WEST);
        panel.add(chatPanel, BorderLayout.CENTER);

        add(panel);
    }

    public void addTask(ToDoItem item) {
        toDoListModel.addElement(item);
    }

    public void addTasks(List<ToDoItem> items) {
        for (ToDoItem item : items) {
            toDoListModel.addElement(item);
        }
    }

    public void displayMessage(Message message) {
        chatArea.append(message.getSender() + ": " + message.getContent() + "\n");
    }

    public String getChatInput() {
        return chatInput.getText();
    }

    public void clearChatInput() {
        chatInput.setText("");
    }

    public ToDoItem getSelectedTask() {
        return toDoList.getSelectedValue();
    }

    public void updateTask(ToDoItem item) {
        int index = toDoListModel.indexOf(item);
        if (index != -1) {
            toDoListModel.set(index, item);
        }
    }

    public void addTaskListener(ActionListener listener) {
        addTaskButton.addActionListener(listener);
    }

    public void addSendMessageListener(ActionListener listener) {
        sendMessageButton.addActionListener(listener);
    }

    public void addDoneButtonListener(ActionListener listener) {
        doneButton.addActionListener(listener);
    }
}

