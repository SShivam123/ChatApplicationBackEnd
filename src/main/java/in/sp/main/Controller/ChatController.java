package in.sp.main.Controller;

import in.sp.main.Service.FileUploadService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import in.sp.main.Exception.AccessDeniedException;
import in.sp.main.Exception.invalidRoomIdException;
import in.sp.main.Repository.KeyRepository;
import in.sp.main.Repository.MessageKeyRepository;
import in.sp.main.Repository.MessageRepository;
import in.sp.main.Repository.UserRepository;
import in.sp.main.Repository.roomMemberRepository;
import in.sp.main.Repository.roomRepository;
import in.sp.main.entity.Message;
import in.sp.main.entity.MessageKey;
import in.sp.main.entity.Room;
import in.sp.main.entity.User;
import in.sp.main.io.MessageRequest;
import in.sp.main.io.TypingEvent;
import in.sp.main.io.UserKeyResponse;
import in.sp.main.io.messageResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
	private final FileUploadService fileUploadService;
	private final roomRepository roomRepository;
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final roomMemberRepository roomMemberRepository;
	private final KeyRepository keyRepository;
	private final MessageKeyRepository messageKeyRepository;
	private final SimpMessagingTemplate messagingTemplate;


	@MessageMapping("/sendMessage/{roomId}")
	public void sendMessage(@DestinationVariable String roomId, MessageRequest request, Principal principal)
			throws IOException {
		if (principal == null) {
			throw new AccessDeniedException("User does not authencated..");
		}
		String userEmail = principal.getName();

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("user not in this email " + userEmail));

		Room room = roomRepository.findByRoomId(request.getRoomId())
				.orElseThrow(() -> new invalidRoomIdException("Room not found withthis id " + request.getRoomId()));
		if (!roomMemberRepository.existsByRoomIdAndUserId(roomId, user.getUserId())) {
			throw new AccessDeniedException("User does not have an access with this email..");
		}
		Message message = new Message();
		message.setContent(request.getContent());
		message.setUser(user);
		message.setType(request.getType());
		message.setSentAt(LocalDateTime.now());
		message.setRoom(room);
		message.setImageUrl(request.getImageUrl());
		message.setIv(request.getIv());
		Message DBMessage = messageRepository.save(message);

		Map<String, String> encyptedKey = request.getEncryptedKeys();
		if (encyptedKey == null || encyptedKey.isEmpty()) {
			return;
		}
		List<MessageKey> keylist = encyptedKey.entrySet().stream().map(entry -> {
			MessageKey messageKey = new MessageKey();
			messageKey.setMessage(DBMessage);
			messageKey.setUserId(entry.getKey());
			messageKey.setEcriptedkey(entry.getValue());
			return messageKey;
		}).collect(Collectors.toList());

		messageKeyRepository.saveAll(keylist);
		List<String> memberIds = new ArrayList<>(encyptedKey.keySet());
		List<User> allMembers = userRepository.findByUserIdIn(memberIds);

		Map<String, String> userIdToEmail = allMembers.stream()
				.collect(Collectors.toMap(User::getUserId, User::getEmail));
		for (Map.Entry<String, String> entry : encyptedKey.entrySet()) {
			String email = userIdToEmail.get(entry.getKey());
			System.out.println("userId: " + entry.getKey() + " → email: " + email);
			messageResponse response = convertToResponse(DBMessage, entry.getValue());
			messagingTemplate.convertAndSendToUser(email, "/queue/room/" + roomId, response);
		}
	}

	private messageResponse convertToResponse(Message message, String Key) {
		System.out.println(Key);
		return messageResponse.builder().content(message.getContent()).imageUrl(message.getImageUrl())
				.senderImageUrl(message.getUser().getImgUrl()).type(message.getType())
				.senderName(message.getUser().getName()).senderUserId(message.getUser().getUserId()).iv(message.getIv())
				.myEncriptedkey(Key).sentAt(message.getSentAt()).build();
	}

//	@MessageMapping("/sendMessage/{roomId}")
//	@SendTo("/topic/room/{roomId}")
//	public Message sendMessage(@DestinationVariable String roomId, @RequestBody MessageRequest request, Principal principal) {
//	    if (principal == null) {
//	        throw new AccessDeniedException("User does not authenticated..");
//	    }
//	    String userEmail = principal.getName();
//	    
//	    User user = userRepository.findByEmail(userEmail)
//	            .orElseThrow(() -> new UsernameNotFoundException("user not in this email " + userEmail));
//	    
//	    Room room = roomRepository.findByRoomId(request.getRoomId())
//	            .orElseThrow(() -> new invalidRoomIdException("Room not found with this id " + request.getRoomId()));
//	            
//	    if (!roomMemberRepository.existsByRoomIdAndUserId(roomId, user.getUserId())) {
//	        throw new AccessDeniedException("User does not have an access with this email..");
//	    }
//	    
//	    Message message = new Message();
//	    message.setContent(request.getContent());
//	    message.setSenderId(user.getUserId());
//	    message.setSenderName(user.getName());
//	    message.setSentAt(LocalDateTime.now());
//	    message.setRoom(room);
//	    message.setSenderImg(user.getImgUrl()); 
//
//	    return messageRepository.save(message);
//	}	

	
//	@PostMapping("/uploadImage")
//	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException{
//		if(file!= null && !file.isEmpty()) {
//			String fileName  = UUID.randomUUID().toString()+"."+StringUtils.getFilenameExtension(file.getOriginalFilename());
//			Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
//			Files.createDirectories(uploadPath);
//			Path targetLocation = uploadPath.resolve(fileName);
//			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//			String imgUrl = "http://localhost:8080/uploads/"+fileName;
//			return ResponseEntity.ok(Map.of("imgUrl", imgUrl));
//		}else {
//			return ResponseEntity.status(400).body(Map.of("message", "Image is Empty"));
//		}
//	}

	@PostMapping("/uploadImage")
	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		if (file != null && !file.isEmpty()) {
			try {
				String imgUrl = fileUploadService.uploadFile(file);
				return ResponseEntity.ok(Map.of("imgUrl", imgUrl));
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(400).body(Map.of("message", "Some error occured.."));
			}
		} else {
			return ResponseEntity.status(400).body(Map.of("message", "Image is Empty"));
		}
	}

	@MessageMapping("/typing/{roomId}")
	public void typingIndicator(@DestinationVariable String roomId, @Payload TypingEvent event, Principal principal) {
		if (principal == null) {
			throw new AccessDeniedException("User is not authenticated");
		}
		String userEmail = principal.getName();
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not exist with this email" + userEmail));
		event.setRoomId(roomId);
		event.setUserId(user.getUserId());
		event.setName(user.getName());
		messagingTemplate.convertAndSend("/topic/room/" + roomId + "/typing", event);
	}

}
