package Socket;
// LoginController.java
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class LoginController {
    private LoginView loginView;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Database database;

    public LoginController(LoginView loginView, Socket socket, ObjectOutputStream out, ObjectInputStream in, Database database) {
        this.loginView = loginView;
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.database = database;

        this.loginView.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String username = loginView.getUsername();
                    String password = loginView.getPassword();
                    User user = database.authenticate(username, password);
                    if (user != null) {
                        out.writeObject(user);
                        out.flush();

                        loginView.setVisible(false);
                        MainView mainView = new MainView();
                        mainView.setVisible(true);
                        new MainController(mainView, socket, out, in, user, database);
                    } else {
                        JOptionPane.showMessageDialog(loginView, "Login failed");
                    }
                } catch (SQLException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}