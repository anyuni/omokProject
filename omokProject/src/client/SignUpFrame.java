package client;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.toedter.calendar.JDateChooser;
import DB.dbConnection;

import java.awt.Font;

public class SignUpFrame extends JFrame {
	private JTextField name; // 사용자 이름
	private JTextField id; // 아이디
	private JPasswordField password; // 비밀번호 입력
	private JPasswordField password_1; // 비밀번호 재입력
	private JComboBox<String> hp_prefix; // 전화번호 앞자리 선택 드롭박스 추가
	private JTextField hp_mid; // 전화번호 중간 부분
	private JTextField hp_end; // 전화번호 뒷부분
	private JTextField frontemail; // 이메일 앞부분
	private JTextField backemail; // 이메일 뒷부분
	private JComboBox<String> emailscroll;
	private JComboBox<String> sex; // 성별
	private JDateChooser birth; // 생년월일 선택용
	private JTextField postaladdress; // 우편번호 필드
	private JButton searchAddressButton; // 주소 검색 버튼
	private JTextArea roadnameaddress; // 도로명 주소 표시용 필드
	private JTextField detailFIeld; // 상세 주소 입력 필드
	private File selectedFile; // 선택된 이미지 파일
	private JLabel profilePicLabel; // 프로필 사진을 표시할 JLabel
	private JLabel passwordMatchLabel; // 비밀번호 일치 여부를 표시할 JLabel
	private JLabel passwordStrengthLabel; // 비밀번호 안정성 검사 레이블
	private JProgressBar passwordStrengthBar; // 비밀번호 안정성 표시 바
	private JTextField nickname;
	private JButton btnCancel;
	private JButton btnReset;
	private JLabel lblNewLabel_1;

	public void setAddress(String postalCode, String roadName) {
		postaladdress.setText(postalCode); // 우편번호 설정
		roadnameaddress.setText(roadName); // 도로명 주소 설정
	}

	public SignUpFrame() {
		setTitle("회원 가입");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 581, 601);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 549, 546);
		panel.setBackground(new Color(240, 240, 240));
		getContentPane().add(panel);
		panel.setLayout(null);

		JButton duplicate_id = new JButton("중복 확인");
		duplicate_id.setBounds(387, 55, 92, 23);
		panel.add(duplicate_id);

		JButton duplicate_nickname = new JButton("중복 확인");
		duplicate_nickname.setBounds(387, 83, 92, 23);
		panel.add(duplicate_nickname);

		JLabel lblPassword_1 = new JLabel("비밀번호 확인:");
		lblPassword_1.setBounds(196, 134, 100, 31);
		panel.add(lblPassword_1);

		password_1 = new JPasswordField();
		password_1.setBounds(286, 139, 91, 20);
		panel.add(password_1);

		// 전화번호 입력 부분
		JLabel lblHp = new JLabel("H.P:");
		lblHp.setBounds(200, 208, 60, 15);
		panel.add(lblHp);

		// 전화번호 앞자리 선택 드롭박스
		String[] phonePrefix = { "010", "011", "016", "017", "018", "019", "02", "031" };
		hp_prefix = new JComboBox<>(phonePrefix); // hp_prefix를 여기서 초기화
		hp_prefix.setBounds(249, 203, 60, 23);
		panel.add(hp_prefix);

		// 전화번호 중간 입력
		hp_mid = new JTextField();
		hp_mid.setBounds(316, 203, 60, 23);
		panel.add(hp_mid);

		// 전화번호 뒷자리 입력
		hp_end = new JTextField();
		hp_end.setBounds(384, 203, 60, 23);
		panel.add(hp_end);

		// 이메일 입력 부분
		JLabel lblEmail = new JLabel("E-Mail:");
		lblEmail.setBounds(198, 262, 46, 15);
		panel.add(lblEmail);

		// 이메일 앞부분 입력
		frontemail = new JTextField();
		frontemail.setBounds(249, 259, 96, 21);
		panel.add(frontemail);

		// 이메일 @
		JLabel lblNewLabel = new JLabel("@");
		lblNewLabel.setBounds(351, 265, 24, 15);
		panel.add(lblNewLabel);

		// 이메일 뒷부분 입력
		backemail = new JTextField();
		backemail.setBounds(368, 259, 96, 21);
		panel.add(backemail);

		// 이메일 뒷부분 선택 드롭박스
		String[] emailDomains = { "직접 입력", "gmail.com", "naver.com", "daum.net", "yahoo.com" };
		emailscroll = new JComboBox<>(emailDomains);
		emailscroll.setBounds(467, 259, 80, 21);
		panel.add(emailscroll);

		emailscroll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 선택된 도메인에 따라 backemail 필드에 자동 입력
				String selectedDomain = (String) emailscroll.getSelectedItem();
				if (selectedDomain.equals("직접 입력")) {
					backemail.setText(""); // 직접 입력을 선택하면 비워줌
				} else {
					backemail.setText(selectedDomain); // 선택된 도메인 입력
				}
			}
		});

		// 비밀번호 확인 레이블 추가
		passwordMatchLabel = new JLabel("");
		passwordMatchLabel.setBounds(201, 162, 300, 20); // 위치 조정
		panel.add(passwordMatchLabel);

		// 비밀번호 확인 필드의 이벤트 리스너 추가
		password_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkPasswordMatch();
			}
		});

		// 아이디 중복 확인 메서드 추가
		duplicate_id.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkDuplicateId();
			}
		});

		// 닉네임 중복 확인 메서드 추가
		duplicate_nickname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkDuplicateNickname();
			}
		});

		// 성별 입력 부분
		JLabel lblGender = new JLabel("성별:");
		lblGender.setBounds(198, 235, 60, 15);
		panel.add(lblGender);

		// 성별 선택 드롭박스
		String[] genderOptions = { "남성", "여성" };
		sex = new JComboBox<>(genderOptions);
		sex.setBounds(249, 233, 100, 21);
		panel.add(sex);

		// 생일 입력 부분
		JLabel lblBirthDate = new JLabel("생일:");
		lblBirthDate.setBounds(197, 180, 60, 15);
		panel.add(lblBirthDate);

		birth = new JDateChooser();
		birth.setBounds(249, 177, 150, 20);
		panel.add(birth);

		// 주소 입력 부분
		JLabel lblAddress = new JLabel("주소:");
		lblAddress.setBounds(198, 300, 60, 15);
		panel.add(lblAddress);

		postaladdress = new JTextField();
		postaladdress.setBounds(249, 293, 122, 23);
		panel.add(postaladdress);

		// 주소 검색 버튼 이벤트 처리 추가
		searchAddressButton = new JButton("우편번호 찾기");
		searchAddressButton.setBounds(381, 293, 122, 25);
		panel.add(searchAddressButton);

		// 도로명 주소 입력 필드
		JLabel lblRoadAddress = new JLabel("도로명:");
		lblRoadAddress.setBounds(198, 324, 60, 23);
		panel.add(lblRoadAddress);

		// 스크롤을 지원하기 위해 JScrollPane으로 감싸기
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(249, 325, 248, 50);
		panel.add(scrollPane);

		roadnameaddress = new JTextArea();
		scrollPane.setViewportView(roadnameaddress);
		roadnameaddress.setLineWrap(true);
		roadnameaddress.setWrapStyleWord(true);
		roadnameaddress.setEditable(false);

		detailFIeld = new JTextField();
		detailFIeld.setBounds(255, 380, 242, 23);
		panel.add(detailFIeld);

		// 프로필 사진 업로드 버튼 추가
		JButton uploadImageButton = new JButton("프로필 사진 업로드");
		uploadImageButton.setBounds(22, 170, 150, 25); // 위치 조정
		panel.add(uploadImageButton);

		// 프로필 사진을 표시할 JLabel 추가
		profilePicLabel = new JLabel();
		profilePicLabel.setBounds(20, 29, 150, 128); // 위치 조정
		profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panel.add(profilePicLabel);

		// 슬라이더 생성 (최소값 50%, 최대값 100%, 초기값 50%)
		JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 10, 100, 50);
		sizeSlider.setBounds(22, 208, 150, 50); // 슬라이더의 위치와 크기 설정
		sizeSlider.setMajorTickSpacing(20); // 큰 눈금 간격 설정
		sizeSlider.setMinorTickSpacing(10); // 작은 눈금 간격 설정
		sizeSlider.setPaintTicks(true); // 눈금을 그리도록 설정
		sizeSlider.setPaintLabels(true); // 값 라벨 표시
		panel.add(sizeSlider);

        // 슬라이더 값 변경 리스너
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int sliderValue = sizeSlider.getValue();
                if (selectedFile != null) {
                    try {
                        BufferedImage originalImage = ImageIO.read(selectedFile);
                        
                        // 슬라이더 값에 따라 새로운 크기 계산
                        int newWidth = originalImage.getWidth() * sliderValue / 100;
                        int newHeight = originalImage.getHeight() * sliderValue / 100;

                        // 이미지 크기 변경
                        Image resizedImage = resizeImage(selectedFile, newWidth, newHeight);

                        // 이미지가 중앙에 고정된 상태로 크기 조절
                        if (resizedImage != null) {
                            updateProfileImage(resizedImage);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


		// 비밀번호 안정성 표시 바 추가
		passwordStrengthBar = new JProgressBar(0, 100);
		passwordStrengthBar.setBounds(392, 113, 102, 20); // 위치 및 크기 설정
		passwordStrengthBar.setValue(0); // 초기값은 0
		passwordStrengthBar.setStringPainted(true); // 진행 상태를 텍스트로 표시
		panel.add(passwordStrengthBar);

		JLabel lblRoadAddress_1 = new JLabel("상세주소:");
		lblRoadAddress_1.setBounds(198, 380, 60, 23);
		panel.add(lblRoadAddress_1);

		searchAddressButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// AddressSearchFrame 인스턴스를 생성하고 표시
				AddressSearchFrame addressSearchFrame = new AddressSearchFrame(SignUpFrame.this);
				addressSearchFrame.setVisible(true);
			}
		});

		JButton btnSignUp = new JButton("가입하기");
		btnSignUp.setFont(new Font("함초롬돋움", Font.BOLD, 12));
		btnSignUp.setBounds(57, 509, 93, 23);
		panel.add(btnSignUp);

		// ActionListener 추가
		btnSignUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				signUpUser();
			}
		});

		// 업로드 버튼 이벤트 처리
		uploadImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedFile = fileChooser.getSelectedFile();
					// 이미지 크기를 150x150으로 조절하여 JLabel에 표시
					Image resizedImage = resizeImage(selectedFile, 150, 150);
					if (resizedImage != null) {
						profilePicLabel.setIcon(new ImageIcon(resizedImage));
					}
				}
			}
		});

		// 비밀번호 안정성 검사 레이블 추가
		passwordStrengthLabel = new JLabel("비밀번호 안정성 검사");
		passwordStrengthLabel.setFont(new Font("함초롬돋움", Font.BOLD, 12));
		passwordStrengthLabel.setBounds(393, 137, 138, 20); // 비밀번호 입력 필드 위에 위치
		passwordStrengthLabel.setForeground(Color.RED); // 기본 색상을 빨강으로 설정
		panel.add(passwordStrengthLabel);

		// UI 요소 초기화 및 설정
		JLabel lblName = new JLabel("이름:");
		lblName.setBounds(196, 29, 60, 15);
		panel.add(lblName);

		name = new JTextField();
		name.setBounds(239, 26, 137, 20);
		panel.add(name);

		JLabel lblId = new JLabel("아이디:");
		lblId.setBounds(196, 59, 60, 15);
		panel.add(lblId);

		id = new JTextField();
		id.setBounds(244, 56, 132, 20);
		panel.add(id);

		password = new JPasswordField();
		password.setBounds(260, 111, 116, 20);
		panel.add(password);

		JLabel lblPassword = new JLabel("비밀번호:");
		lblPassword.setBounds(196, 114, 60, 15);
		panel.add(lblPassword);

		nickname = new JTextField();
		nickname.setBounds(245, 84, 130, 21);
		panel.add(nickname);
		nickname.setColumns(10);

		JLabel lblnickname = new JLabel("닉네임:");
		lblnickname.setBounds(196, 89, 60, 15);
		panel.add(lblnickname);

		btnCancel = new JButton("취소");
		btnCancel.setVerticalAlignment(SwingConstants.TOP);
		btnCancel.setFont(new Font("함초롬돋움", Font.BOLD, 12));
		btnCancel.setBounds(252, 510, 93, 23);
		panel.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); // 현재 창을 닫음
				MainFrame mainFrame = new MainFrame(); // MainFrame 객체 생성
				mainFrame.setVisible(true); // MainFrame 표시
			}
		});

		btnReset = new JButton("다시입력");
		btnReset.setFont(new Font("함초롬돋움", Font.BOLD, 12));
		btnReset.setBounds(438, 510, 93, 23);
		panel.add(btnReset);
		
		lblNewLabel_1 = new JLabel("프로필 사진");
		lblNewLabel_1.setBounds(62, 10, 83, 15);
		panel.add(lblNewLabel_1);
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				id.setText("");
				password.setText("");
				password_1.setText("");
				hp_mid.setText("");
				hp_end.setText("");
				frontemail.setText("");
				backemail.setText("");
				postaladdress.setText("");
				roadnameaddress.setText("");
				detailFIeld.setText("");
				nickname.setText("");
				profilePicLabel.setIcon(null); // 프로필 사진 초기화
				passwordStrengthBar.setValue(0); // 비밀번호 강도 바 초기화
				passwordMatchLabel.setText(""); // 비밀번호 확인 초기화
			}
		});

		// 비밀번호 입력 필드에도 이벤트 리스너 추가
		password.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkPasswordMatch();
				checkPasswordStrength();
			}
		});

	}

	// 이미지 리사이즈 메서드
    private Image resizeImage(File file, int newWidth, int newHeight) {
        try {
            BufferedImage originalImage = ImageIO.read(file);
            Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            return resizedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	// 이미지 크기 조절 후 JLabel에 이미지 설정
	private void updateProfileImage(Image image) {
	    ImageIcon imageIcon = new ImageIcon(image);
	    profilePicLabel.setIcon(imageIcon);
	    
	    // 이미지가 중앙에 위치하도록 설정
	    profilePicLabel.setHorizontalAlignment(JLabel.CENTER);
	    profilePicLabel.setVerticalAlignment(JLabel.CENTER);
	}
	

	// 회원가입 기능 구현
	private void signUpUser() {
		String userId = id.getText().trim();
		String userName = name.getText().trim();
		String userPassword = new String(password.getPassword()).trim();
		String userPasswordConfirm = new String(password_1.getPassword()).trim();
		String userHpPrefix = (String) hp_prefix.getSelectedItem();
		String userHpMid = hp_mid.getText().trim();
		String userHpEnd = hp_end.getText().trim();
		String userFrontEmail = frontemail.getText().trim();
		String userBackEmail = backemail.getText().trim();
		String userEmailDomain = (String) emailscroll.getSelectedItem();
		String userEmail = userFrontEmail + "@" + (userEmailDomain.equals("직접 입력") ? userBackEmail : userEmailDomain);
		String userSex = (String) sex.getSelectedItem();
		String usernickname = nickname.getText();

		// 비밀번호가 안정성 조건을 만족하는지 확인
		if (!isPasswordStrong(userPassword)) {
			JOptionPane.showMessageDialog(this, "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자 중 3가지 이상을 포함해야 합니다.", "비밀번호 오류",
					JOptionPane.ERROR_MESSAGE);
			return; // 조건을 만족하지 않으면 회원가입 진행 중단
		}

		// 생일을 설정
		java.util.Date birthDate = birth.getDate();
		String userBirth = birthDate != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(birthDate) : null;

		String userPostalAddress = postaladdress.getText().trim();
		String userRoadNameAddress = roadnameaddress.getText().trim();
		String userDetailAddress = detailFIeld.getText().trim();

		if (userId.isEmpty() || userName.isEmpty() || userPassword.isEmpty() || userPasswordConfirm.isEmpty()
				|| userHpMid.isEmpty() || userHpEnd.isEmpty() || userFrontEmail.isEmpty() || userBackEmail.isEmpty()
				|| userPostalAddress.isEmpty() || userRoadNameAddress.isEmpty() || userDetailAddress.isEmpty()) {
			JOptionPane.showMessageDialog(this, "모든 정보를 채워야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!userPassword.equals(userPasswordConfirm)) {
			JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// DB에 회원가입 정보를 저장하는 로직
		dbConnection connection = new dbConnection();
		try (Connection conn = connection.getConnection()) {
			String sql = "INSERT INTO user (id, password, name, hp_prefix, hp_mid, hp_end, frontemail, backemail, sex, birth, postaladdress, roadnameaddress, detailladdress, profile, nickname) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, userId);
				pstmt.setString(2, userPassword);
				pstmt.setString(3, userName);
				pstmt.setString(4, userHpPrefix);
				pstmt.setString(5, userHpMid);
				pstmt.setString(6, userHpEnd);
				pstmt.setString(7, userFrontEmail);
				pstmt.setString(8, userBackEmail);
				pstmt.setString(9, userSex);
				pstmt.setString(10, userBirth);
				pstmt.setString(11, userPostalAddress);
				pstmt.setString(12, userRoadNameAddress);
				pstmt.setString(13, userDetailAddress);
				pstmt.setString(15, usernickname);

				// 프로필 사진이 선택되었을 경우 이미지 파일을 바이트 배열로 읽어오기
				if (selectedFile != null) {
					try (FileInputStream profileImageInputStream = new FileInputStream(selectedFile)) {
						byte[] profileImageBytes = profileImageInputStream.readAllBytes();
						pstmt.setBytes(14, profileImageBytes);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, "프로필 사진 저장 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					// 디폴트 이미지 처리 로직 추가
					try (InputStream defaultImageStream = getClass().getResourceAsStream("/image/normal.png")) {
						byte[] defaultImageBytes = defaultImageStream.readAllBytes();
						pstmt.setBytes(14, defaultImageBytes);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, "디폴트 프로필 사진 설정 중 오류가 발생했습니다.", "오류",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);

				// 메인프레임으로 이동
				dispose(); // 현재 창 닫기
				MainFrame mainFrame = new MainFrame();
				mainFrame.setVisible(true); // 로비창 표시
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "회원가입 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "데이터베이스 연결 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 아이디 중복 체크 메서드
	private void checkDuplicateId() {
		String userId = id.getText().trim();

		if (userId.isEmpty()) {
			JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}

		dbConnection connection = new dbConnection();
		try (Connection conn = connection.getConnection()) {
			String sql = "SELECT COUNT(*) FROM user WHERE id = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, userId);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next() && rs.getInt(1) > 0) {
						// 중복된 아이디가 존재함
						JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.", "아이디 중복", JOptionPane.WARNING_MESSAGE);
					} else {
						// 사용 가능한 아이디
						JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.", "아이디 확인",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "중복 확인 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "데이터베이스 연결 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 닉네임 중복 확인 기능 추가
	private void checkDuplicateNickname() {
		String userNickname = nickname.getText().trim();
		if (userNickname.isEmpty()) {
			JOptionPane.showMessageDialog(this, "닉네임을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}

		dbConnection connection = new dbConnection();
		try (Connection conn = connection.getConnection()) {
			String sql = "SELECT COUNT(*) FROM user WHERE nickname = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, userNickname);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next() && rs.getInt(1) > 0) {
						JOptionPane.showMessageDialog(this, "닉네임이 이미 존재합니다.", "중복 확인", JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, "사용 가능한 닉네임입니다.", "중복 확인", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 비밀번호 일치 여부를 체크하는 메서드
	private void checkPasswordMatch() {
		String passwordText = new String(password.getPassword());
		String passwordConfirmText = new String(password_1.getPassword());

		if (passwordText.equals(passwordConfirmText)) {
			passwordMatchLabel.setText("비밀번호가 일치합니다.");
			passwordMatchLabel.setForeground(Color.GREEN); // 일치할 경우 글자색을 초록색으로
		} else {
			passwordMatchLabel.setText("비밀번호가 일치하지 않습니다.");
			passwordMatchLabel.setForeground(Color.RED); // 불일치할 경우 글자색을 빨간색으로
		}
	}

	// 비밀번호 안정성 조건 메서드
	private void checkPasswordStrength() {
		String passwordText = new String(password.getPassword());

		int strength = 0;
		int length = passwordText.length();

		// 비밀번호 길이 조건: 8자 이상
		if (length >= 8) {
			strength += 25;
		}

		// 대문자 포함 여부
		if (passwordText.matches(".*[A-Z].*")) {
			strength += 25;
		}

		// 숫자 포함 여부
		if (passwordText.matches(".*\\d.*")) {
			strength += 25;
		}

		// 특수문자 포함 여부
		if (passwordText.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
			strength += 25;
		}

		// ProgressBar 업데이트
		passwordStrengthBar.setValue(strength);

		// 강도에 따라 텍스트 색상 변경 (강한 비밀번호일수록 초록색)
		if (strength < 50) {
			passwordStrengthLabel.setForeground(Color.RED);
			passwordStrengthLabel.setText("비밀번호가 약합니다.");
		} else if (strength < 75) {
			passwordStrengthLabel.setForeground(Color.ORANGE);
			passwordStrengthLabel.setText("비밀번호가 보통입니다.");
		} else {
			passwordStrengthLabel.setForeground(Color.GREEN);
			passwordStrengthLabel.setText("비밀번호가 강력합니다.");
		}
	}

	private boolean isPasswordStrong(String password) {
		int conditionCount = 0;

		// 길이 검사 (8자 이상)
		if (password.length() >= 8) {
			conditionCount++;
		}

		// 대문자 포함 검사
		if (password.matches(".*[A-Z].*")) {
			conditionCount++;
		}

		// 소문자 포함 검사
		if (password.matches(".*[a-z].*")) {
			conditionCount++;
		}

		// 숫자 포함 검사
		if (password.matches(".*[0-9].*")) {
			conditionCount++;
		}

		// 특수문자 포함 검사
		if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
			conditionCount++;
		}

		// 조건을 3개 이상 만족하면 true 반환
		return conditionCount > 3;
	}

	// Main Method
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				SignUpFrame frame = new SignUpFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
