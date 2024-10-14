package client;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import DB.DBManager;

public class MainFrame extends JFrame {

    private JTextField textFieldId;
    private JPasswordField passwordField; // 비밀번호 필드
    private Image backgroundImage;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame window = new MainFrame();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainFrame() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/image/omokback.png")); // 배경 이미지 경로 설정
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialize();
    }

    private void initialize() {
        setTitle("오목 게임");
        setBounds(100, 100, 700, 453);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(700, 453));

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setBounds(0, 0, 700, 453);  // 패널 크기 설정
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false); // 투명 설정
        mainPanel.setLayout(null); // 절대 위치 사용
        mainPanel.setBounds(0, 0, 700, 453); // 크기 설정
        layeredPane.add(mainPanel, JLayeredPane.PALETTE_LAYER);

        JPanel loginPanel = new JPanel();
        loginPanel.setSize(307, 93);

        int x = (layeredPane.getWidth() - loginPanel.getWidth()) / 2;
        int y = (layeredPane.getHeight() - loginPanel.getHeight()) / 2;
        loginPanel.setLocation(187, 299);

        loginPanel.setBackground(new Color(255, 255, 255, 150)); // 반투명 배경
        loginPanel.setLayout(null);
        mainPanel.add(loginPanel);

        JLabel lblId = new JLabel("아이디    :");
        lblId.setBounds(10, 10, 65, 15);
        loginPanel.add(lblId);

        JLabel lblPw = new JLabel("패스워드 :");
        lblPw.setBounds(10, 35, 83, 15);
        loginPanel.add(lblPw);

        textFieldId = new JTextField();
        textFieldId.setBounds(83, 7, 135, 21);
        loginPanel.add(textFieldId);
        textFieldId.setColumns(10);

        // 비밀번호 필드를 JPasswordField로 변경
        passwordField = new JPasswordField();
        passwordField.setColumns(10);
        passwordField.setBounds(83, 32, 135, 21);
        loginPanel.add(passwordField);

        JButton loginBtn = new JButton("로그인");
        loginBtn.setFont(new Font("함초롬돋움", Font.PLAIN, 12));
        loginBtn.setBounds(223, 6, 72, 47);
        loginPanel.add(loginBtn);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = textFieldId.getText();
                String password = new String(passwordField.getPassword()); // 비밀번호 가져오기

                if (DBManager.login(id, password)) {
                    // 로그인 성공 시 바로 LobbyFrame으로 이동
                    new LobbyFrame().setVisible(true);
                    dispose(); // 현재 창 닫기
                } else {
                    JOptionPane.showMessageDialog(null, "로그인 실패! 아이디와 비밀번호를 확인하세요.");
                }
            }
        });

        JButton signupBtn = new JButton("회원가입");
        signupBtn.setFont(new Font("함초롬돋움", Font.PLAIN, 12));
        signupBtn.setBounds(83, 60, 89, 19);
        loginPanel.add(signupBtn);

        signupBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SignUpFrame().setVisible(true);
                dispose();
            }
        });

        JButton exitBtn = new JButton("게임 종료");
        exitBtn.setBounds(206, 60, 89, 19);
        loginPanel.add(exitBtn);
        exitBtn.setFont(new Font("함초롬돋움", Font.PLAIN, 12));

        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "정말로 게임을 종료하시겠습니까?", "게임 종료",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        getContentPane().add(layeredPane, BorderLayout.CENTER);
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
