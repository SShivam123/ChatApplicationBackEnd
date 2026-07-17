package in.sp.main.Service;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import in.sp.main.Exception.EmailAllredyExistException;
import in.sp.main.Exception.EmailNotExistException;
import in.sp.main.Exception.InvalidEmailEception;
import in.sp.main.Exception.InvlaidOtpException;
import in.sp.main.Exception.InvlaidOtpTokenException;
import in.sp.main.Exception.OtpExpireException;
import in.sp.main.Repository.EmailVerificationRepo;
import in.sp.main.Repository.ResetPasswordRepo;
import in.sp.main.Repository.UserKeyRepository;
import in.sp.main.Repository.UserRepository;
import in.sp.main.entity.EmailVerification;
import in.sp.main.entity.ResetPassword;
import in.sp.main.entity.User;
import in.sp.main.entity.UserKey;
import in.sp.main.io.EmailService;
import in.sp.main.io.RestPasswordRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final EmailVerificationRepo emailVerificationRepo;
	private final EmailService emailService;
	private final ResetPasswordRepo resetPasswordRepo;
	private final PasswordEncoder passwordEncoder;
	private final UserKeyRepository userKeyRepository;

	public void sendVerifyOtp(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new EmailAllredyExistException("This email is already exist...");
		}
		
		Optional<EmailVerification> emailVerification2 = emailVerificationRepo.findByEmail(email);
		if (emailVerification2.isPresent()) {
			emailVerificationRepo.delete(emailVerification2.get());
		}
		
		String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
		Long expiry = System.currentTimeMillis() + (1000 * 60 * 60 * 24);
		EmailVerification emailVerification = new EmailVerification();
		emailVerification.setEmail(email);
		emailVerification.setOtp(otp);
		emailVerification.setExpiry(expiry);
		emailVerification.setVerified(false);
		emailVerificationRepo.save(emailVerification);

		try {
			emailService.sendVerifyOtp(email, otp);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException("unable to send email...");
		}
	}

	public void verifyOtp(Map<String, String> request) {
		System.out.println(request);
		String email = request.get("email");
		String otp = request.get("otp");
		if (email == null || otp == null) {
			throw new RuntimeException("Please enter the otp and email..");
		}
		EmailVerification emailVerification = emailVerificationRepo.findByEmail(email)
				.orElseThrow(() -> new InvalidEmailEception("Invlaid email.."));
		String DBotp = emailVerification.getOtp();
		Long Expiry = emailVerification.getExpiry();
		if (!otp.equals(DBotp)) {
			throw new InvlaidOtpException("Invalid otp.");
		}
		if (System.currentTimeMillis() > Expiry) {
			throw new OtpExpireException("Otp Expire..");
		}
		emailVerification.setVerified(true);
		emailVerificationRepo.save(emailVerification);
	}

	public void sendResetLink(String email) {
		if (!userRepository.existsByEmail(email)) {
			throw new EmailNotExistException("This email not registered with us...");
		}
		Optional<ResetPassword> resetpassword = resetPasswordRepo.findByEmail(email);
		if(resetpassword.isPresent()) {
			resetPasswordRepo.delete(resetpassword.get());
		}
		
		Long expiry = System.currentTimeMillis() + (1000 * 60 *5);
		String token = UUID.randomUUID().toString();
		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setEmail(email);
		resetPassword.setExpiry(expiry);
		resetPassword.setToken(token);
		resetPasswordRepo.save(resetPassword);
		emailService.sendResetLink(email,token); 
	}

	
	@Transactional
	public void resetPassword(RestPasswordRequest request) {
		if(request.getPassword().trim().length()<6) {
			throw new IllegalArgumentException("Password must atleast 6 character..");
		}
		ResetPassword resetPassword = resetPasswordRepo.findByToken(request.getToken()).orElseThrow(()-> new InvlaidOtpTokenException("Invlaid token.."));
		if(System.currentTimeMillis()>resetPassword.getExpiry()) {
			throw new InvlaidOtpTokenException("This link is expire..");
		}
		User user = userRepository.findByEmail(resetPassword.getEmail()).orElseThrow(()-> new UsernameNotFoundException("User not found in this email.."+ resetPassword.getEmail()));
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		UserKey userKey = userKeyRepository.findByUserId(user.getUserId()).orElseThrow(()-> new  UsernameNotFoundException("User not exist with this id.."));
		userKey.setEnceyptedprivateKey(request.getEncryptedPrivateKey());
		userKey.setIv(request.getIv());
		userKey.setSalt(request.getSalt());
		userKey.setPublicKey(request.getPublicKey());
		userKeyRepository.save(userKey);
		userRepository.save(user);
	}

}
