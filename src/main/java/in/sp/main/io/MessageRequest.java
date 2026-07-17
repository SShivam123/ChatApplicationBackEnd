package in.sp.main.io;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
	private String content;
	private String roomId;
	private String type;
	private String imageUrl;
	private String iv;
	private HashMap<String, String> encryptedKeys;
}
