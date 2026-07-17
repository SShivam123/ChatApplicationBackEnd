package in.sp.main.Controller;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.sp.main.Service.UserService;
import in.sp.main.entity.User;
import in.sp.main.io.ChangePasswordRequest;
import in.sp.main.io.MemberResponseForUsers;
import in.sp.main.io.RoomResponse;
import in.sp.main.io.UserRegisterRequest;
import in.sp.main.io.UserResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	@PostMapping("/register")
	public ResponseEntity<UserResponse> userRegister(@RequestBody UserRegisterRequest request) {
		UserResponse userResponse = userService.userRegister(request);
		return new ResponseEntity<UserResponse>(userResponse,HttpStatus.CREATED);
	}
	
	@GetMapping("/profile")
	public ResponseEntity<UserResponse> getProfile(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email =auth.getName();
		UserResponse response = userService.getProfile(email);
		return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<UserResponse>> searchByEmail(@RequestParam String email){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loginUserEmail =auth.getName();
		List<UserResponse> users = userService.searchByEmail(email,loginUserEmail);
		return new ResponseEntity<List<UserResponse>>(users,HttpStatus.OK);
	}
	
	@GetMapping("/getRoomUser")
	public ResponseEntity<List<MemberResponseForUsers>> getRoomUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loginUserEmail =auth.getName();
		List<MemberResponseForUsers> users = userService.getRoomUser(loginUserEmail);
		return new ResponseEntity<List<MemberResponseForUsers>>(users,HttpStatus.OK);
	}
	
	@PostMapping("/editprofile")
	public ResponseEntity<UserResponse> editProfile(@RequestParam String name,@RequestParam(required = false) MultipartFile image) throws IOException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email =auth.getName();
		UserResponse response = userService.editProfile(name,image,email);
		return new ResponseEntity<UserResponse>(response,HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteAccount/user")
	public ResponseEntity<?>deleteAccount(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loginUserEmail =auth.getName();
		userService.deleteAccount(loginUserEmail);
		return ResponseEntity.status(204).build();
	}
	
	@PutMapping("/changePasword")
	public ResponseEntity<?> changePasword(@RequestBody ChangePasswordRequest request){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loginUserEmail =auth.getName();
		userService.changePasword(loginUserEmail,request);
		return ResponseEntity.status(200).build();
	}
}
