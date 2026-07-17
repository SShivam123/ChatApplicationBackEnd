package in.sp.main.io;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class messageResponse {
	private String content;
	private LocalDateTime sentAt;
	private String senderUserId;
	private String senderName;
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String senderImageUrl;
	private String type;
	private String imageUrl;
	private String iv;
	private String myEncriptedkey;
}
