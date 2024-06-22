package Socket;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<ObjectOutputStream> clientOutputStreams = new HashSet<>();
    public static Database database;

    public static void main(String[] args) {
        try {
            database = new Database();
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running...");

            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private User user;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Đăng nhập
                user = (User) in.readObject();
                User authenticatedUser = database.authenticate(user.getUsername(), user.getPassword());
                if (authenticatedUser != null) {
                    out.writeObject(true);
                    synchronized (clientOutputStreams) {
                        clientOutputStreams.add(out);
                    }

                    // Gửi danh sách công việc hiện tại cho người dùng
                    List<ToDoItem> items = database.getToDoItems(authenticatedUser);
                    for (ToDoItem item : items) {
                        out.writeObject(item);
                        out.flush();
                    }

                } else {
                    out.writeObject(false);
                    socket.close();
                    return;
                }

                // Nhận và xử lý thông điệp từ client
                Object obj;
                while ((obj = in.readObject()) != null) {
                    if (obj instanceof ToDoItem) {
                        ToDoItem item = (ToDoItem) obj;
                        database.addToDoItem(authenticatedUser, item);
                        broadcast(obj);
                    } else if (obj instanceof Message) {
                        broadcast(obj);
                    }
                }
            } catch (IOException | ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    synchronized (clientOutputStreams) {
                        clientOutputStreams.remove(out);
                    }
                    System.out.println(user.getUsername() + " has disconnected.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(Object obj) {
            synchronized (clientOutputStreams) {
                for (ObjectOutputStream clientOut : clientOutputStreams) {
                    try {
                        clientOut.writeObject(obj);
                        clientOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
