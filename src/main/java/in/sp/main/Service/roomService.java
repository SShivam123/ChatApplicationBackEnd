package in.sp.main.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import in.sp.main.Exception.AccessDeniedException;
import in.sp.main.Exception.RoomAlreadyExistException;
import in.sp.main.Exception.invalidRoomIdException;
import in.sp.main.Repository.UserRepository;
import in.sp.main.Repository.roomMemberRepository;
import in.sp.main.Repository.roomRepository;
import in.sp.main.entity.Message;
import in.sp.main.entity.MessageKey;
import in.sp.main.entity.Room;
import in.sp.main.entity.User;
import in.sp.main.entity.roomMembers;
import in.sp.main.io.RoomResponse;
import in.sp.main.io.messageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class roomService {
	private final roomRepository roomRepository;
	private final UserRepository userRepository;
	private final roomMemberRepository roomMemberRepository;

	public RoomResponse createRoom(String roomId,String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user not in this email " + email));
		if (roomRepository.existsByRoomId(roomId)) {
			throw new RoomAlreadyExistException("Room is Already Exist");
		} else {
			Room room = new Room();
			room.setRoomId(roomId);
			room.setCreatedBy(user);
			user.getRooms().add(room);
			Room Dbroom = roomRepository.save(room);
			roomMembers members = new roomMembers();
			members.setEmail(user.getEmail());
			members.setRole("ADMIN");
			members.setRoomId(Dbroom.getRoomId());
			members.setUserId(user.getUserId());
			roomMemberRepository.save(members);
			return convertToResponse(Dbroom);
		}
	}

	public RoomResponse getRoom(String roomId,String email) {
		Room room = roomRepository.findByRoomId(roomId)
				.orElseThrow(() -> new invalidRoomIdException("Room does not exist in this Id.."));
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user not in this email " + email));
		if(!roomMemberRepository.existsByRoomIdAndEmail(roomId,user.getEmail())) {
			throw new AccessDeniedException("User does not have acess to enter the room contact the admin of the room");
		}
		return convertToResponse(room);
	}

	
	private RoomResponse convertToResponse(Room room) {
		Integer member = roomMemberRepository.countByRoomId(room.getRoomId());
		return RoomResponse.builder()
				.adminName(room.getCreatedBy().getName())
				.userId(room.getCreatedBy().getUserId())
				.createdAt(room.getCreatedBy().getCreatedAt())
				.roomId(room.getRoomId())
				.members(member)
				.build();
	}

	public List<messageResponse> getAllMessage(String roomId,String email) {
		Room room = roomRepository.findByRoomId(roomId)
				.orElseThrow(() -> new invalidRoomIdException("Room does not exist in this Id.."));
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user not in this email " + email));
		if(!roomMemberRepository.existsByRoomIdAndUserId(roomId,user.getUserId())) {
			throw new AccessDeniedException("User does not have acess to enter the room contact the admin of the room");
		}
//		List<Message> allmessages = room.getMessages();
		return room.getMessages().stream().map(message->convertToResponse(message, user)).collect(Collectors.toList());	
	}
	
	
	private messageResponse convertToResponse(Message message,User user) {
		String myEncryptedKey = message.getMessageKeys().stream()
		        .filter(key -> key.getUserId().equals(user.getUserId()))
		        .map(MessageKey::getEcriptedkey)
		        .findFirst()
		        .orElse(null);
		return messageResponse.builder()
				.content(message.getContent())
				.imageUrl(message.getImageUrl())
				.senderImageUrl(message.getUser().getImgUrl())
				.type(message.getType())
				.senderName(message.getUser().getName())
				.senderUserId(message.getUser().getUserId())
				.iv(message.getIv())
				.myEncriptedkey(myEncryptedKey)
				.sentAt(message.getSentAt())
				.build();
	}
	

	public List<RoomResponse> getRoom(String loginUserEmail) {
		User user = userRepository.findByEmail(loginUserEmail).orElseThrow(()-> new UsernameNotFoundException("User not found with this email "+loginUserEmail));
		List<Room> rooms = user.getRooms();
		return rooms.stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	public List<RoomResponse> getAllRoomCreatedOrJoin(String loginUserEmail) {
		User user = userRepository.findByEmail(loginUserEmail)
				.orElseThrow(() -> new UsernameNotFoundException("user not in this email " + loginUserEmail));
		List<Room> userRoomsCreatedOrJoin=roomMemberRepository.findByUserId(user.getUserId());
		return userRoomsCreatedOrJoin.stream().map(this::convertToResponse).collect(Collectors.toList());
		
	}
	
	@Transactional
	public void deleteRoom(String roomId, String loggedInUserEmail) {
		User user = userRepository.findByEmail(loggedInUserEmail).orElseThrow(()-> new UsernameNotFoundException("User not found with this email " + loggedInUserEmail));
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new invalidRoomIdException("Room Doesn't exists with this id"));
		if(!(room.getCreatedBy().getUserId().equals(user.getUserId()))) {
			throw new AccessDeniedException("User Does'nt have access to delete the room");
		}
		roomRepository.delete(room);
		roomMemberRepository.deleteAllByRoomId(roomId);
	}
	
}
