package in.sp.main.Controller;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import in.sp.main.Service.roomService;
import in.sp.main.io.NewRoomRequest;
import in.sp.main.io.RoomResponse;
import in.sp.main.io.messageResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class roomController {
	private final roomService roomService;
	
	@PostMapping("/create-room")
	public ResponseEntity<RoomResponse> createRoom(@RequestBody NewRoomRequest request){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String Email = auth.getName();
			RoomResponse response = roomService.createRoom(request.getRoomId(),Email);
		return new ResponseEntity<RoomResponse>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/room/{roomId}")
	public ResponseEntity<RoomResponse> getRoom (@PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		RoomResponse response = roomService.getRoom(roomId,email);
		return new ResponseEntity<RoomResponse>(response,HttpStatus.OK);
	}
	
	@GetMapping("/all-message/{roomId}")
	public ResponseEntity<List<messageResponse>> getAllMessage(@PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		List<messageResponse> message = roomService.getAllMessage(roomId,email);
		return new ResponseEntity<List<messageResponse>>(message,HttpStatus.OK);
	}
	
	@GetMapping("/getRoom")
	public ResponseEntity<List<RoomResponse>> getRoom(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loginUserEmail =auth.getName();
		List<RoomResponse> allRooms = roomService.getRoom(loginUserEmail);
		return new ResponseEntity<List<RoomResponse>>(allRooms,HttpStatus.OK);
	}
	
	@GetMapping("/getAllRoomCreatedOrJoin")
	public ResponseEntity<List<RoomResponse>> getAllRoomCreatedOrJoin(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loginUserEmail =auth.getName();
		List<RoomResponse> allRooms = roomService.getAllRoomCreatedOrJoin(loginUserEmail);
		return new ResponseEntity<List<RoomResponse>>(allRooms,HttpStatus.OK);
	}
	
	
	@DeleteMapping("/delete/room/{roomId}")
	public ResponseEntity<?> deleteUser(@PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		roomService.deleteRoom(roomId, loggedInUserEmail);
		return ResponseEntity.status(204).build();
	}
}
