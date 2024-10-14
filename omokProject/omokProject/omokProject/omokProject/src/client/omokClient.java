package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class omokClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame; // GUI 프레임
    private JTextField inputField; // 메시지 입력 필드
    private JTextArea chatArea; // 채팅 내용을 표시할 텍스트 영역

    public omokClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // GUI 설정
            frame = new JFrame("오목 게임 클라이언트");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());

            chatArea = new JTextArea();
            chatArea.setEditable(false); // 사용자 입력 불가능
            frame.add(new JScrollPane(chatArea), BorderLayout.CENTER); // 스크롤 가능하게 추가

            inputField = new JTextField();
            frame.add(inputField, BorderLayout.SOUTH); // 입력 필드를 하단에 추가

            // 전송 버튼 추가
            JButton sendButton = new JButton("전송");
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage(inputField.getText()); // 메시지 전송
                    inputField.setText(""); // 입력 필드 초기화
                }
            });
            frame.add(sendButton, BorderLayout.EAST); // 버튼을 하단 오른쪽에 추가

            frame.setVisible(true); // 프레임 표시

            new Thread(new MessageReceiver()).start(); // 서버로부터 메시지 수신 스레드 시작
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            out.println(message); // 서버로 메시지 전송
        }
    }

    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                String messageFromServer;
                while ((messageFromServer = in.readLine()) != null) {
                    chatArea.append("서버: " + messageFromServer + "\n"); // 채팅 영역에 메시지 추가
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new omokClient("localhost", 12345); // 서버 주소와 포트 설정
    }
}
