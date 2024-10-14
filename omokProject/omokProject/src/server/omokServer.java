package server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class omokServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private Connection dbConnection;

    public omokServer(int port) {
        try {
            // DB 연결 설정
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3307/omok", "root", "1234"); // root 계정 사용

            serverSocket = new ServerSocket(port);
            clients = new ArrayList<>();
            System.out.println("서버가 포트 " + port + "에서 시작되었습니다.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("클라이언트로부터 받은 메시지: " + message);
                    // DB에 메시지 저장
                    saveMessageToDB(message);
                    // 모든 클라이언트에게 메시지 전달
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveMessageToDB(String message) {
            try {
                String sql = "INSERT INTO chat_log (message) VALUES (?)";
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                stmt.setString(1, message);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void broadcastMessage(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }
    }

    public static void main(String[] args) {
        new omokServer(12345); // 서버 포트 설정
    }
}
