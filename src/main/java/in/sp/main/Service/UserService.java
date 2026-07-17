package in.sp.main.Service;

import java.util.Base64;
import in.sp.main.filter.JwtFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import in.sp.main.Exception.AccessDeniedException;
import in.sp.main.Exception.EmailAllredyExistException;
import in.sp.main.Exception.EmailNotVerifiedException;
import in.sp.main.Exception.KeynotExistException;
import in.sp.main.Exception.passwordMismatchException;
import in.sp.main.Repository.EmailVerificationRepo;
import in.sp.main.Repository.MessageRepository;
import in.sp.main.Repository.UserKeyRepository;
import in.sp.main.Repository.UserRepository;
import in.sp.main.Repository.roomMemberRepository;
import in.sp.main.Repository.roomRepository;
import in.sp.main.entity.EmailVerification;
import in.sp.main.entity.Room;
import in.sp.main.entity.User;
import in.sp.main.entity.UserKey;
import in.sp.main.entity.roomMembers;
import in.sp.main.io.ChangePasswordRequest;
import in.sp.main.io.MemberResponseForUsers;
import in.sp.main.io.RoomResponse;
import in.sp.main.io.UserRegisterRequest;
import in.sp.main.io.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final JwtFilter jwtFilter;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final roomMemberRepository roomMemberRepository;
	private final roomRepository roomRepository;
	private final MessageRepository messageRepository;
	private final UserkeyService userkeyService;
	private final UserKeyRepository userKeyRepository;
	private final EmailVerificationRepo emailVerificationRepo;
	private final FileUploadService fileUploadService;

	@Transactional
	public UserResponse userRegister(UserRegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAllredyExistException("Email already exist with this email " + request.getEmail());
		}
		Optional<EmailVerification> emailVerification = emailVerificationRepo.findByEmail(request.getEmail());
		if(emailVerification.isPresent()) {
			if(!emailVerification.get().getVerified()) {
				throw new EmailNotVerifiedException("Please first email verify..");
			}
		}else {
			throw new EmailNotVerifiedException("Email is not verified..");
		}
		
		User user = convertToEntity(request);
		User DBuser = userRepository.save(user);
		userkeyService.uploadkey(DBuser.getEmail(), request.getPublicKey(), request.getEncryptedPrivateKey(),
				request.getIv(), request.getSalt());
		return convertToresponse(DBuser);
	}

	public User convertToEntity(UserRegisterRequest request) {
		return User.builder().email(request.getEmail()).name(request.getName())
				.password(passwordEncoder.encode(request.getPassword())).userId(UUID.randomUUID().toString()).build();
	}

	public UserResponse convertToresponse(User user) {
		return UserResponse.builder().email(user.getEmail()).name(user.getName()).createdAt(user.getCreatedAt())
				.userId(user.getUserId()).imgUrl(user.getImgUrl()).build();
	}

	public UserResponse getProfile(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found in this email " + email));
		return convertToresponse(user);
	}

	public List<UserResponse> searchByEmail(String searchEmail, String loginUserEmail) {
		if (!userRepository.existsByEmail(loginUserEmail)) {
			throw new AccessDeniedException("Not access to user");
		}
		List<User> users = userRepository.findByEmailContainingIgnoreCase(searchEmail);
		return users.stream().map(this::convertToresponse).collect(Collectors.toList());
	}

	public List<MemberResponseForUsers> getRoomUser(String loginUserEmail) {
		User user = userRepository.findByEmail(loginUserEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found in this email " + loginUserEmail));
		return roomMemberRepository.findAllUsersRooms(user.getUserId());
	}

//	public UserResponse editProfile(String name, MultipartFile image, String email) throws IOException {
//		User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found in this email "+email));
//		String fileName = UUID.randomUUID().toString()+"."+StringUtils.getFilenameExtension(image.getOriginalFilename());
//		Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
//		Files.createDirectories(uploadPath);
//		Path targetLocation = uploadPath.resolve(fileName);
//		Files.copy(image.getInputStream(), targetLocation,StandardCopyOption.REPLACE_EXISTING);
//		String imgUrl = "/uploads/"+fileName;
//		user.setImgUrl(imgUrl);
//		if(name!=null) {
//			user.setName(name);
//		}
//		userRepository.save(user);
//		return convertToresponse(user);
//	}

	public UserResponse editProfile(String name, MultipartFile image, String email) throws IOException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		if (name != null && !name.trim().isEmpty()) {
			user.setName(name);
		}
		if (image != null && !image.isEmpty()) {
			try {
				String imgUrl = fileUploadService.uploadFile(image);
				user.setImgUrl(imgUrl);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Someyhing went wrong ..");
			}
		}
		userRepository.save(user);
		return convertToresponse(user);
	}

	@Transactional
	public void deleteAccount(String loginUserEmail) {
		User user = userRepository.findByEmail(loginUserEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		messageRepository.setUserToNull(user.getUserId());
		List<Room> allRooms = user.getRooms();
		for (Room room : allRooms) {
			List<roomMembers> roomMember = roomMemberRepository.findOtherMember(user.getUserId(), room.getRoomId());
			if (!roomMember.isEmpty()) {
				String userId = roomMember.get(0).getUserId();
				User userEntity = userRepository.findByUserId(userId)
						.orElseThrow(() -> new UsernameNotFoundException("user not found in this id.." + userId));
				room.setCreatedBy(userEntity);
				roomRepository.save(room);
				roomMembers members = roomMember.get(0);
				members.setRole("ADMIN");
				roomMemberRepository.save(members);
			} else {
				roomMemberRepository.deleteAllByRoomId(room.getRoomId());
				roomRepository.delete(room);
			}

		}
		roomMemberRepository.deleteAllByUserId(user.getUserId());
		userRepository.delete(user);
	}

	@Transactional
	public void changePasword(String loginUserEmail, ChangePasswordRequest request) {
		User user = userRepository.findByEmail(loginUserEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found in this email " + loginUserEmail));
		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			throw new IllegalArgumentException("New password cannot be the same as your current password");
		}
		if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
			UserKey userKey = userKeyRepository.findByUserId(user.getUserId()).orElseThrow(
					() -> new KeynotExistException("Key Not exists with this user id : " + user.getUserId()));
			userKey.setEnceyptedprivateKey(request.getEncryptedKey());
			userKey.setIv(request.getIv());
			userKey.setSalt(request.getSalt());
			userKeyRepository.save(userKey);
			userRepository.save(user);
		} else {
			throw new passwordMismatchException("Old password is invalid");
		}

	}
}
