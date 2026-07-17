package in.sp.main.Controller;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import in.sp.main.Repository.UserRepository;
import in.sp.main.Service.UserkeyService;
import in.sp.main.entity.UserKey;
import in.sp.main.io.KeyRequest;
import in.sp.main.io.PrivateKeyResponse;
import in.sp.main.io.UserKeyResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserKyeController {
	private final UserRepository userRepository;
	private final UserkeyService userkeyService;
	
//	@PostMapping("/keys")
//	public ResponseEntity<?> uploadkey(@RequestBody KeyRequest request) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		String email =auth.getName();
//		userkeyService.uploadkey(email,request.getPublicKey());
//		return ResponseEntity.status(201).body("public key save successfully");
//	}
	
	
	@GetMapping("/keys/{userId}")
	public ResponseEntity<Map<String,String>> getPublicKey(@PathVariable String userId){
		UserKey userKey = userkeyService.getPublicKey(userId);
		return new ResponseEntity<Map<String,String>>(Map.of("publickey",userKey.getPublicKey()),HttpStatus.OK);
	}
	
	@GetMapping("/key/{roomId}")
	public ResponseEntity<List<UserKeyResponse>> getAllKeysByRoomId(@PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email =auth.getName();
		List<UserKeyResponse> userIdAndKeys= userkeyService.getAllKeysByRoomId(email,roomId);
		System.out.println(userIdAndKeys);
		return new ResponseEntity<List<UserKeyResponse>>(userIdAndKeys,HttpStatus.OK);
	}
	
	@GetMapping("/privateKey/user")
	public ResponseEntity<PrivateKeyResponse> getPrivateKey(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return new ResponseEntity<PrivateKeyResponse>(userkeyService.getPrivateKey(email), HttpStatus.OK);
	}
}
