package in.sp.main.Service;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import in.sp.main.Exception.AccessDeniedException;
import in.sp.main.Exception.UserAlReadyExistException;
import in.sp.main.Exception.invalidRoomIdException;
import in.sp.main.Repository.UserRepository;
import in.sp.main.Repository.roomMemberRepository;
import in.sp.main.Repository.roomRepository;
import in.sp.main.entity.Room;
import in.sp.main.entity.User;
import in.sp.main.entity.roomMembers;
import in.sp.main.io.roomMemberResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class roomMemberService {
	private final UserRepository userRepository;
	private final roomMemberRepository roomMemberRepository;
	private final roomRepository roomRepository;

	public roomMemberResponse addMember(String MemberEmail, String roomCreaterEmail,String roomId) {
		User adminUser = userRepository.findByEmail(roomCreaterEmail)
				.orElseThrow(() -> new UsernameNotFoundException("user not in this email " + roomCreaterEmail));
		System.out.println(roomCreaterEmail);
		System.out.println(roomId);
		if(!roomMemberRepository.existsByEmailAndRoleAndRoomId(roomCreaterEmail,"ADMIN",roomId)) {
			throw new AccessDeniedException("User do not have access add the another user "+roomId);
		}
		
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new invalidRoomIdException("This room is notexist in this id "+roomId));
		User user = userRepository.findByEmail(MemberEmail)
				.orElseThrow(() -> new UsernameNotFoundException("user not in this email " + MemberEmail));
		if(roomMemberRepository.existsByRoomIdAndEmail(roomId, MemberEmail)) {
			throw new UserAlReadyExistException("User already exist in this Id " + roomId);
		}
		
		roomMembers members = convertToEntity(user,room.getRoomId());
		roomMembers DBmember = roomMemberRepository.save(members);
		return convertToResponse(DBmember);
	}

	private roomMemberResponse convertToResponse(roomMembers dBmember) {
		return roomMemberResponse.builder()
				.email(dBmember.getEmail())
				.role(dBmember.getRole())
				.roomId(dBmember.getRoomId())
				.userId(dBmember.getUserId())
				.joinedAt(dBmember.getJoinedAt())
				.build();
	}

	private roomMembers convertToEntity(User user,String roomId) {
		return  roomMembers.builder()
				.email(user.getEmail())
				.userId(user.getUserId())
				.roomId(roomId)
				.role("USER")
				.build();
	}

	
	 @Transactional
	public void DeleteMemberInRoom(String roomId,String userId,String loginUserEmail) {
		User user = userRepository.findByEmail(loginUserEmail)
				.orElseThrow(() -> new UsernameNotFoundException("user not in this email " + loginUserEmail));
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new invalidRoomIdException("Room does not exist in this id "));
		if(room.getCreatedBy().getUserId().equals(user.getUserId())) {
			roomMemberRepository.deleteByUserIdAndRoomId(userId,roomId);
		}else {
			throw new AccessDeniedException("Does not have access the deleting user");
		}
	}

}
