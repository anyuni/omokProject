package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Font;

public class LobbyFrame extends JFrame {
	private JList<String> roomList;
	private DefaultListModel<String> listModel;
	private JLabel weatherLabel; // 날씨 정보를 표시할 레이블
	private JLabel weatherImageLabel; // 날씨 이미지를 표시할 레이블

	public LobbyFrame() {
		setTitle("오목 게임 로비");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 664, 615);
		getContentPane().setLayout(null);

		// 날씨 이미지를 표시할 레이블 추가
		weatherImageLabel = new JLabel();
		weatherImageLabel.setBackground(new Color(255, 255, 255));
		weatherImageLabel.setBounds(378, 51, 200, 200); // 이미지 크기에 따라 조정
		getContentPane().add(weatherImageLabel);

		JLabel lblRooms = new JLabel("방 목록:");
		lblRooms.setBounds(30, 20, 60, 15);
		getContentPane().add(lblRooms);

		listModel = new DefaultListModel<>();
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

		// 날씨 정보를 표시할 레이블 추가
		weatherLabel = new JLabel("날씨 정보를 불러오는 중...");
		weatherLabel.setBounds(433, 20, 170, 15);
		getContentPane().add(weatherLabel);

		// 날씨 정보를 불러오기 위한 버튼 추가
		JButton btnGetWeather = new JButton("날씨 정보 업데이트");
		btnGetWeather.setBounds(378, 271, 200, 40);
		getContentPane().add(btnGetWeather);

		JLabel nowWeather = new JLabel("현재 날씨:");
		nowWeather.setBounds(368, 20, 94, 15);
		getContentPane().add(nowWeather);

		// 버튼 클릭 시 날씨 정보 가져오기
		btnGetWeather.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 사용자의 좌표(nx, ny)를 입력받거나 고정 좌표 사용
				String nx = "60"; // 예시 좌표
				String ny = "127"; // 예시 좌표

				// 날씨 정보를 가져와서 레이블에 표시
				String weatherInfo = WeatherAPI.getWeatherInfo(nx, ny, weatherLabel, weatherImageLabel);
				weatherLabel.setText("날씨 정보: " + weatherInfo);
			}
		});
	}

	public static void main(String[] args) {
		new LobbyFrame().setVisible(true);
	}
}