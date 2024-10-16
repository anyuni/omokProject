package client;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class omokClient {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static LobbyFrame lobbyFrame; // LobbyFrame 인스턴스 추가
    

    public omokClient(Socket socket) {
        this.socket = socket;
    }
    
    public Socket getSocket() {
        return socket; // 소켓 변수 반환
    }
    
    public static void setLobbyFrame(LobbyFrame frame) {
        lobbyFrame = frame; // 전달된 LobbyFrame 인스턴스를 설정
    }


    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 12345); // 서버 IP 및 포트 설정
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // GUI 초기화 및 표시
            EventQueue.invokeLater(() -> {
                try {
                    omokClient client = new omokClient(socket); // omokClient 인스턴스 생성
                    LobbyFrame lobbyFrame = new LobbyFrame(client); // LobbyFrame에 클라이언트 전달
                    lobbyFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 서버 메시지 처리 스레드
            new Thread(() -> {
                String response;
                try {
                    while ((response = in.readLine()) != null) {
                        processServerMessage(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void processServerMessage(String message) {
        // 서버에서 보낸 메시지를 처리하는 로직 구현
        if (message.startsWith("CHAT//")) { // 채팅 메시지 처리
            String chatMessage = message.substring(6); // "CHAT//" 부분 제거
            if (lobbyFrame != null) {
                lobbyFrame.appendChatMessage(chatMessage); // LobbyFrame의 메서드를 호출하여 메시지를 추가
            }
        } else {
            // 방 생성 완료, 방 입장 등 다른 메시지 처리 로직 추가 가능
        }
    }

    public void sendMsg(String message) {
        try {
            if (out != null) { // PrintWriter가 초기화되어 있는지 확인
                out.println(message); // 서버로 메시지 전송
            } else {
                System.err.println("PrintWriter가 초기화되지 않았습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String receiveMsg() throws IOException {
        return in.readLine(); // BufferedReader에서 한 줄 읽어오기
    }


}
