package in.sp.main.Controller;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.sp.main.Exception.InvalidEmailEception;
import in.sp.main.Service.AppUserDetailService;
import in.sp.main.Service.AuthService;
import in.sp.main.Service.JwtService;
import in.sp.main.io.AuthRequest;
import in.sp.main.io.AuthResponse;
import in.sp.main.io.RestPasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final AppUserDetailService appUserDetailService;
	private final JwtService jwtService;
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> loginRequest(@Valid @RequestBody AuthRequest request){		
		try {
		authenticate(request.getEmail(),request.getPassword());
		UserDetails userDetails = appUserDetailService.loadUserByUsername(request.getEmail());
		final String jwtToken = jwtService.generateToken(userDetails.getUsername());
		ResponseCookie cookie = ResponseCookie
				.from("jwt",jwtToken)
				.httpOnly(true)
				.path("/")
				.maxAge(Duration.ofDays(1))
				.sameSite("None")
				.secure(true)
				.build();
		return ResponseEntity
				.ok()
				.header(HttpHeaders.SET_COOKIE,cookie.toString())
				.body(new AuthResponse(request.getEmail(),jwtToken));
		
		}catch(BadCredentialsException e) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("message", "invalid userId and password");
			map.put("errors",true);
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}catch(DisabledException e) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("message", "Account is disabled");
			map.put("errors",true);
			return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
		}catch(Exception e) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("message", "Authencation failed");
			map.put("errors",true);
			return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
		}
	}
	
	
	private void authenticate (String email,String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
	}
	
	@PostMapping("/logoutt")
	public ResponseEntity<?> logOut(){
		ResponseCookie cookie = ResponseCookie.from("jwt","")
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(0)
				.sameSite("None")
				.build();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("LogOut Successfully");
	}
	
	@PostMapping("/send-otp")
	public void sendVerifyOtp(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		if(!email.contains("@") || !email.contains(".") || email.contains(" ")){
			throw new InvalidEmailEception("Plese enter the valid email..");
		}
			authService.sendVerifyOtp(email);
	}
	
	
	@PostMapping("/verify-otp")
	public void verifyOtp(@RequestBody Map<String, String> request) {
		authService.verifyOtp(request);
	}
	
	
	@PostMapping("/forgot-password")
	public void forgotpassword(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		if(!email.contains("@") || !email.contains(".") || email.contains(" ")){
			throw new InvalidEmailEception("Plese enter the valid email..");
		}
			authService.sendResetLink(email);
	}
	
	
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody RestPasswordRequest request) {
		authService.resetPassword(request);
		return ResponseEntity.ok().build();
	}
}
