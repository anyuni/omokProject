package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    private JPanel boardPanel;
    private JButton[][] boardButtons;

    public GameFrame() {
        setTitle("오목 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 600);
        getContentPane().setLayout(new BorderLayout());

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(15, 15));
        getContentPane().add(boardPanel, BorderLayout.CENTER);

        boardButtons = new JButton[15][15];

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                boardButtons[i][j] = new JButton("");
                boardPanel.add(boardButtons[i][j]);

                final int x = i;
                final int y = j;

                boardButtons[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // 돌을 두는 로직 (여기서는 임시로 "X"로 표기)
                        boardButtons[x][y].setText("X");
                        boardButtons[x][y].setEnabled(false);  // 돌이 놓인 자리 비활성화
                    }
                });
            }
        }

        // 채팅 영역
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        JTextArea chatArea = new JTextArea(5, 20);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        JTextField chatInput = new JTextField();
        JButton sendButton = new JButton("전송");

        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);
        chatPanel.add(sendButton, BorderLayout.EAST);

        getContentPane().add(chatPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = chatInput.getText();
                if (!message.isEmpty()) {
                    chatArea.append("나: " + message + "\n");
                    chatInput.setText("");  // 채팅 입력창 초기화
                    // 서버로 메시지 전송하는 로직 추가
                }
            }
        });
    }
}
