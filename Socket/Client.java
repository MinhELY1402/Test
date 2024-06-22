package Socket;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.sql.SQLException;


public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            LoginView loginView = new LoginView();
            loginView.setVisible(true);

            // Initialize database connection
            Database database = new Database();

            new LoginController(loginView, socket, out, in, database);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
