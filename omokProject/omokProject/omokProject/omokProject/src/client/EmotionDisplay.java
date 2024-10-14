package client;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class EmotionDisplay {

    private JFrame frame;
    private JLabel emotionLabel;
    private JLabel imageLabel;

    public EmotionDisplay() {
        // JFrame 설정
        frame = new JFrame("Emotion Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(430, 334);
        frame.getContentPane().setLayout(new BorderLayout());

        emotionLabel = new JLabel("", SwingConstants.CENTER);
        emotionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.getContentPane().add(emotionLabel, BorderLayout.NORTH);

        imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 252)); // 원하는 크기로 설정
        frame.getContentPane().add(imageLabel, BorderLayout.CENTER);

        // 감정 표현 선택 (예: 버튼으로)
        JButton happyButton = new JButton("Happy");
        happyButton.addActionListener(e -> displayEmotion("/image/happy.png"));

        JButton sadButton = new JButton("Sad");
        sadButton.addActionListener(e -> displayEmotion("/image/sad.png"));

        JButton angryButton = new JButton("Angry");
        angryButton.addActionListener(e -> displayEmotion("/image/angry.png"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(happyButton);
        buttonPanel.add(sadButton);
        buttonPanel.add(angryButton);

        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        
        emotionLabel.setText("감정표현");
        emotionLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 24)); // 한글 지원 폰트
    }

    private void displayEmotion(String imageFile) {
        // 감정 레이블에 텍스트 설정
       

        // 이미지 로드 및 설정
        ImageIcon emotionImage = new ImageIcon(getClass().getResource(imageFile));
        
        // 이미지가 유효한지 확인
        if (emotionImage.getIconWidth() > 0) {
            imageLabel.setIcon(emotionImage);
        } else {
            JOptionPane.showMessageDialog(frame, "이미지를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }

        // 5초 후에 감정 표현 제거
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    emotionLabel.setText("");
                    imageLabel.setIcon(null);
                });
            }
        }, 5000); // 5000ms = 5초
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmotionDisplay::new);
    }
}
