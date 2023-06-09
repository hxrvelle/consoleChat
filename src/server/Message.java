package server;

import java.sql.*;
import java.util.ArrayList;

public class Message {
    private String msg;
    private int from_user;
    private int to_user;

    public Message(String msg, int from_user, int to_user) {
        this.msg = msg;
        this.from_user = from_user;
        this.to_user = to_user;
    }

    public String getMsg() {
        return msg;
    }

    public int getFrom_user() {
        return from_user;
    }

    public int getTo_user() {
        return to_user;
    }

    public void saveMessage(String db_url, String db_login, String db_pass) throws SQLException {
        Connection connection = DriverManager.getConnection(db_url, db_login, db_pass);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO messages (msg, to_user, from_user) VALUES (?,?,?)"
        );
        preparedStatement.setString(1, this.msg);
        preparedStatement.setInt(2, this.to_user);
        preparedStatement.setInt(3, this.from_user);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    public static ArrayList<Message> readPublicMessages(String db_url, String db_login, String db_pass) throws SQLException{
        Connection connection = DriverManager.getConnection(db_url, db_login, db_pass);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM `messages`, `users` WHERE users.id = messages.from_user AND to_user='0'"
        );
        ArrayList<Message> messages = new ArrayList<>();
        while (resultSet.next()){
            String msg = resultSet.getString("msg");
            String name = resultSet.getString("name");
            int fromUser = resultSet.getInt("from_user");
            int to_user = 0;
            Message message = new Message(name+": "+msg, fromUser, to_user);
            messages.add(message);
        }
        statement.close();
        return messages;
    }

    public static ArrayList<Message> readPrivateMessages(String db_url, String db_login, String db_pass, int fromUser, int toUser) throws SQLException {
        Connection connection = DriverManager.getConnection(db_url, db_login, db_pass);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM `messages`, `users` WHERE user.id = messages.from_user AND (from_user'" + fromUser + "' AND to_user='" + toUser + "' OR from_user='" + toUser + "' AND to_user='" + fromUser + "')"
        );
        ArrayList<Message> messages = new ArrayList<>();
        while (resultSet.next()){
            String msg = resultSet.getString("msg");
            String name = resultSet.getString("name");
            fromUser = resultSet.getInt("from_user");
            int to_user = resultSet.getInt("to_user");
            Message message = new Message(name+": "+msg, fromUser, to_user);
            messages.add(message);
        }
        statement.close();
        return messages;
    }
}