package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class LobbyFrame extends JFrame {
    private static final int WIDTH = 664;
    private static final int HEIGHT = 615;
    private static final String INITIAL_WEATHER_TEXT = "날씨 정보를 불러오는 중...";

    private JList<String> roomList;
    private DefaultListModel<String> listModel;
    private JLabel weatherLabel;
    private JLabel weatherImageLabel;
    private omokClient client; // omokClient 인스턴스
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private Socket socket; // 소켓 변수 추가

    public LobbyFrame(omokClient client) {
        this.client = client; // omokClient 객체 설정
        this.socket = client.getSocket(); // omokcCient에서 Socket 가져오기
        this.listModel = new DefaultListModel<>();

        setTitle("오목 게임 로비");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, WIDTH, HEIGHT);
        getContentPane().setLayout(null);

        initializeUI();
        startMessageReceiver();

        // LobbyFrame 인스턴스를 omokClient에 설정
        omokClient.setLobbyFrame(this);
    }

    private void initializeUI() {
        weatherImageLabel = new JLabel();
        weatherImageLabel.setBackground(Color.WHITE);
        weatherImageLabel.setBounds(378, 51, 200, 200);
        getContentPane().add(weatherImageLabel);

        JLabel lblRooms = new JLabel("방 목록:");
        lblRooms.setBounds(30, 20, 60, 15);
        getContentPane().add(lblRooms);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 50, 325, 219);
        getContentPane().add(scrollPane);
        roomList = new JList<>(listModel);
        scrollPane.setViewportView(roomList);

        JButton btnCreateRoom = new JButton("방 생성");
        btnCreateRoom.setBounds(30, 279, 90, 25);
        getContentPane().add(btnCreateRoom);

        JButton btnJoinRoom = new JButton("방 입장");
        btnJoinRoom.setBounds(130, 279, 90, 25);
        getContentPane().add(btnJoinRoom);

        weatherLabel = new JLabel(INITIAL_WEATHER_TEXT);
        weatherLabel.setBounds(326, 20, 316, 15);
        getContentPane().add(weatherLabel);

        JButton btnGetWeather = new JButton("날씨 정보 업데이트");
        btnGetWeather.setBounds(378, 271, 200, 40);
        getContentPane().add(btnGetWeather);

        JLabel nowWeather = new JLabel("현재 날씨:");
        nowWeather.setBounds(261, 20, 94, 15);
        getContentPane().add(nowWeather);

        btnGetWeather.addActionListener(e -> {
            WeatherAPI.getWeatherInfo(weatherLabel, weatherImageLabel);
        });
        btnCreateRoom.addActionListener(e -> {
            client.sendMsg("CREATE_ROOM");
            updateRoomList();
        });

        btnJoinRoom.addActionListener(e -> {
            String selectedRoom = roomList.getSelectedValue();
            if (selectedRoom != null) {
                client.sendMsg("JOIN_ROOM:" + selectedRoom);
            }
        });

        setupChatComponents();
    }

    private void setupChatComponents() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(400, 300));
        chatScrollPane.setBounds(30, 320, 600, 200);
        getContentPane().add(chatScrollPane);

        JPanel inputPanel = new JPanel();
        inputField = new JTextField(20);
        sendButton = new JButton("전송");
        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        inputPanel.setBounds(30, 530, 600, 30);
        getContentPane().add(inputPanel);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        if (client == null) {
            JOptionPane.showMessageDialog(this, "클라이언트가 초기화되지 않았습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String message = inputField.getText().trim(); // 사용자 입력 메시지 가져오기
        if (!message.isEmpty()) {
            client.sendMsg("CHAT//" + message); // 서버로 메시지 전송
            inputField.setText(""); // 입력 필드 비우기
        }
    }



    // appendChatMessage 메서드 추가
    public void appendChatMessage(String message) {
        chatArea.append(message + "\n");
    }

    private void startMessageReceiver() {
        new Thread(new MessageReceiver()).start();
    }

    class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String message = client.receiveMsg();
                    if (message != null) {
                        appendChatMessage(message); // appendChatMessage 호출
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(LobbyFrame.this, "메시지 수신 중 오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void updateRoomList() {
        client.sendMsg("GET_ROOMS"); // 방 목록 요청
        // TODO: 서버 응답으로 방 목록을 listModel에 추가하는 로직 추가
    }

    private String getWeather(double nx, double ny) {
        // 날씨 정보를 가져오는 로직 구현 필요
        return "맑음"; // 예시로 고정된 날씨 정보 반환
    }

    private void updateWeatherInfo(String weather) {
        weatherLabel.setText(weather);
        switch (weather) {
            case "맑음":
                weatherImageLabel.setIcon(new ImageIcon(getClass().getResource("/image/sunny.png")));
                break;
            case "비":
                weatherImageLabel.setIcon(new ImageIcon(getClass().getResource("/image/rainy.png")));
                break;
            case "눈":
                weatherImageLabel.setIcon(new ImageIcon(getClass().getResource("/image/snow.png")));
                break;
            case "흐림":
                weatherImageLabel.setIcon(new ImageIcon(getClass().getResource("/image/cloudy.png")));
                break;
            default:
                weatherImageLabel.setIcon(null);
        }
    }
}
