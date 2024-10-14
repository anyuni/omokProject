package client;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Random;
public class EmailVerification {
	private String generatedCode;
	
	//랜덤 6자리 숫자 생성
	public String generateVerificationCode() {
		Random random = new Random();
		int verificationCode = 100000+random.nextInt(900000);
		generatedCode = String.valueOf(verificationCode);
		return generatedCode;
	}
	
	//이메일 보내기
	public void sendVerificationEmail(String recipientEmail) {
		String from = "oopp2314eehh@gmail.com";
		String password = "wjdals0324jm!@";
		
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com"); // Gmail SMTP 서버
		properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getInstance(properties, new Authenticator() {
        	@Override
        	protected PasswordAuthentication getPasswordAuthentication() {
        		return new PasswordAuthentication(from, password);
        	}
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Verification Code");
            message.setText("Your verification code is: " + generatedCode);

            Transport.send(message);
            System.out.println("Verification email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // 생성된 코드를 반환하는 메소드
    public String getGeneratedCode() {
        return generatedCode;
    }
}