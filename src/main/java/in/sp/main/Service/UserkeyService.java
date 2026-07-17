package in.sp.main.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import in.sp.main.Exception.AccessDeniedException;
import in.sp.main.Exception.KeynotExistException;
import in.sp.main.Exception.invalidRoomIdException;
import in.sp.main.Repository.KeyRepository;
import in.sp.main.Repository.UserRepository;
import in.sp.main.Repository.roomMemberRepository;
import in.sp.main.Repository.roomRepository;
import in.sp.main.entity.Room;
import in.sp.main.entity.User;
import in.sp.main.entity.UserKey;
import in.sp.main.entity.roomMembers;
import in.sp.main.io.PrivateKeyResponse;
import in.sp.main.io.UserKeyResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserkeyService {
	private final UserRepository userRepository;
	private final KeyRepository keyRepository;
	private final roomRepository roomRepository;
	private final roomMemberRepository memberRepository;
	
	public UserKey uploadkey(String email,String publicKey, String encryptedPrivateKey,String iv, String salt) {
		User  user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exist in this email "+email));
		Optional<UserKey> userKeyy = keyRepository.findByUserId(user.getUserId());
		if(userKeyy.isPresent()) {
			userKeyy.get().setPublicKey(publicKey);
			return keyRepository.save(userKeyy.get());
		}
		UserKey userKey = new UserKey();
		userKey.setUserId(user.getUserId());
		userKey.setGeneratedAt(LocalDateTime.now());
		userKey.setPublicKey(publicKey);
		userKey.setEnceyptedprivateKey(encryptedPrivateKey);
		userKey.setIv(iv);
		userKey.setSalt(salt);
		return keyRepository.save(userKey);
	}
	
	public UserKey getPublicKey(String userId) {
		return keyRepository.findByUserId(userId).orElseThrow(()-> new KeynotExistException("Key not exist in this id..."));
	}

	
	public List<UserKeyResponse> getAllKeysByRoomId(String email, String roomId) {
		User  user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exist in this email "+email));
		Room room = roomRepository.findByRoomId(roomId)
				.orElseThrow(() -> new invalidRoomIdException("Room does not exist in this Id.."));
		
		if(!memberRepository.existsByRoomIdAndUserId(roomId,user.getUserId())) {
			throw new AccessDeniedException("you have dont access to enter the room..");
		}
		List<roomMembers> roomMembers = memberRepository.findAllByRoomId(roomId);
		
		List<String> userIds = roomMembers.stream().map(member -> member.getUserId()).collect(Collectors.toList());
		System.out.println(userIds.size());
		List<UserKey> userIdAndKey = keyRepository.findByUserIdIn(userIds);
		System.out.println(userIdAndKey.size());
		return userIdAndKey.stream().map(key ->{
			return UserKeyResponse.builder()
					.userId(key.getUserId())
					.publicKey(key.getPublicKey())
					.build();
		}).collect(Collectors.toList());
		
	}

	public  PrivateKeyResponse getPrivateKey(String email) {
		User userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exists withn this email " + email));
		UserKey userKey = keyRepository.findByUserId(userEntity.getUserId()).orElseThrow(()-> new KeynotExistException("Key Not exists with this userId " + userEntity.getUserId()));
		return PrivateKeyResponse.builder()
				.iv(userKey.getIv())
				.privateKey(userKey.getEnceyptedprivateKey())
				.salt(userKey.getSalt())
				.build();
	}

}
