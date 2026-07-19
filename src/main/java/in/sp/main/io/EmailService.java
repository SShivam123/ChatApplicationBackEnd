package in.sp.main.io;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender javaMailSender;
	
	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;
	@Value("${brevo.api.key}")
    private String brevoApiKey;

	public void sendVerifyOtp(String email, String otp) {
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//		mailMessage.setFrom(fromEmail);
//		mailMessage.setTo(email);
//		mailMessage.setSubject("Verify email otp for chitChat application ");
//		mailMessage.setText("Hello," + email + "\n Your otp for verify the account is " + otp+"\n User this otp is verify the account \n\n Regards, \n chitchatApplication");
//		javaMailSender.send(mailMessage);
		String url = "https://api.brevo.com/v3/smtp/email";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey); 

        String jsonBody = "{"
                + "\"sender\":{\"email\":\"" + fromEmail + "\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"Verify Email OTP\","
                + "\"htmlContent\":\"<p>Hello " + email
                + ",<br><br>Your OTP is <b>" + otp + "</b>"
                + "<br><br>Regards,<br>ChitChat Application</p>\""
                + "}";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("Email Sent via API! " + response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Unable to Send Email: " + e.getMessage());
        }
    }
	

	public void sendResetLink(String email, String token) {
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//		mailMessage.setFrom(fromEmail);
//		mailMessage.setTo(email);
//		mailMessage.setSubject("Reset password Link..");
//		mailMessage.setText("Hello," + email + "\n we a recieved a request to reset your password. "+ "\n Click the button or link below to create a new password :\n\n" +"http://localhost:5173/reset-password?token="+token+ "\n This link expire will 5 minutes on ChitChatApplication");
//		javaMailSender.send(mailMessage);
//	}
		String url = "https://api.brevo.com/v3/smtp/email";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey); 

        String resetLink = "https://chat-application-front-end-five.vercel.app/reset-password?token=" + token;

String jsonBody = "{"
        + "\"sender\":{\"email\":\"" + fromEmail + "\"},"
        + "\"to\":[{\"email\":\"" + email + "\"}],"
        + "\"subject\":\"Reset Password\","
        + "\"htmlContent\":\"<p>Hello " + email
        + ",<br><br>We received a request to reset your password."
        + "<br><br><a href='" + resetLink + "'>Click here to reset your password</a>"
        + "<br><br>This link will expire in 5 minutes."
        + "<br><br>Regards,<br>ChitChat Application</p>\""
        + "}";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("Email Sent via API! " + response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Unable to Send Email: " + e.getMessage());
        }
	}
}
