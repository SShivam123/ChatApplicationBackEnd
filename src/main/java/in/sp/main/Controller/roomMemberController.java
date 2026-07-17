package in.sp.main.Controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import in.sp.main.Repository.UserRepository;
import in.sp.main.Repository.roomMemberRepository;
import in.sp.main.Service.roomMemberService;
import in.sp.main.entity.User;
import in.sp.main.entity.roomMembers;
import in.sp.main.io.RoomResponse;
import in.sp.main.io.roomMemberRequest;
import in.sp.main.io.roomMemberResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class roomMemberController {
	private final UserRepository userRepository;
	private final roomMemberService roomMemberService;
	
	@PostMapping("add-member")
	public ResponseEntity<roomMemberResponse> addMember(@RequestBody roomMemberRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roomCreaterEmail = auth.getName();
		String email = request.getEmail();
		roomMemberResponse memberResponse = roomMemberService.addMember(email,roomCreaterEmail,request.getRoomId());
		return new ResponseEntity<roomMemberResponse>(memberResponse,HttpStatus.CREATED);
	}
	
	
	@DeleteMapping("/delete/room/{roomId}/Member/{userId}")
	public ResponseEntity<?> DeleteMemberInRoom(@PathVariable String roomId , @PathVariable String userId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loginUserEmail =auth.getName();
		roomMemberService.DeleteMemberInRoom(roomId,userId,loginUserEmail);
		return ResponseEntity.noContent().build();
	}
}