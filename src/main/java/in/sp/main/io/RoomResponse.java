package in.sp.main.io;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {
	private String roomId;
	private String userId;
	private String adminName;
	private LocalDateTime createdAt;
	private Integer members;
}
