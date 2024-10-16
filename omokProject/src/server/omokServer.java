package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class omokServer {
    private static final int PORT = 12345; // 포트 번호
    private static final Set<PrintWriter> clientWriters = new HashSet<>();
    private static final Map<String, Set<PrintWriter>> rooms = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("서버 시작...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start(); // 새로운 클라이언트 처리
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String roomName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    processMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                    if (roomName != null && rooms.containsKey(roomName)) {
                        rooms.get(roomName).remove(out);
                        if (rooms.get(roomName).isEmpty()) {
                            rooms.remove(roomName); // 방이 비어있으면 삭제
                        }
                    }
                }
            }
        }

        private void processMessage(String message) {
            if (message.startsWith("CREATE_ROOM")) {
                createRoom();
            } else if (message.startsWith("JOIN_ROOM:")) {
                joinRoom(message.substring(10));
            }
        }

        private void createRoom() {
            String roomName = "Room" + (rooms.size() + 1);
            rooms.put(roomName, new HashSet<>());
            out.println("ROOM_CREATED:" + roomName);
        }

        private void joinRoom(String roomName) {
            if (rooms.containsKey(roomName)) {
                rooms.get(roomName).add(out);
                this.roomName = roomName;
                out.println("JOINED_ROOM:" + roomName);
                // 방에 있는 다른 사용자에게 알림
                for (PrintWriter writer : rooms.get(roomName)) {
                    writer.println("USER_JOINED:" + roomName);
                }
            } else {
                out.println("ROOM_NOT_FOUND");
            }
        }
    }
}
