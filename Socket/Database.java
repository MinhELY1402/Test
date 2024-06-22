package Socket;
// Database.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/todolist";
    private static final String USER = "root"; // Thay đổi nếu cần
    private static final String PASSWORD = ""; // Thay đổi nếu cần

    private Connection connection;

    public Database() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public User authenticate(String username, String password) throws SQLException {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"));
            }
        }
        return null;
    }

    public List<ToDoItem> getToDoItems(User user) throws SQLException {
        List<ToDoItem> items = new ArrayList<>();
        String query = "SELECT * FROM todo_items WHERE user_id = (SELECT id FROM user WHERE username = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ToDoItem item = new ToDoItem(rs.getString("task"), rs.getBoolean("done"));
                items.add(item);
            }
        }
        return items;
    }

    public void addToDoItem(User user, ToDoItem item) throws SQLException {
        String query = "INSERT INTO todo_items (user_id, task, done) VALUES ((SELECT id FROM user WHERE username = ?), ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, item.getTask());
            stmt.setBoolean(3, item.isDone());
            stmt.executeUpdate();
        }
    }

    public void updateToDoItem(User user, ToDoItem item) throws SQLException {
        String query = "UPDATE todo_items SET done = ? WHERE user_id = (SELECT id FROM user WHERE username = ?) AND task = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, item.isDone());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, item.getTask());
            stmt.executeUpdate();
        }
    }
}
