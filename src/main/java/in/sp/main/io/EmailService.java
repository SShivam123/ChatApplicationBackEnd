package in.sp.main.io;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender javaMailSender;
	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;

	public void sendVerifyOtp(String email, String otp) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(fromEmail);
		mailMessage.setTo(email);
		mailMessage.setSubject("Verify email otp for chitChat application ");
		mailMessage.setText("Hello," + email + "\n Your otp for verify the account is " + otp
				+ "\n User this otp is verify the account \n\n Regards, \n chitchatApplication");
		javaMailSender.send(mailMessage);
	}

	public void sendResetLink(String email, String token) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(fromEmail);
		mailMessage.setTo(email);
		mailMessage.setSubject("Reset password Link..");
		mailMessage.setText("Hello," + email + "\n we a recieved a request to reset your password. "+ "\n Click the button or link below to create a new password :\n\n" +"http://localhost:5173/reset-password?token="+token+ "\n This link expire will 5 minutes on ChitChatApplication");
		javaMailSender.send(mailMessage);
	}
}
