package in.sp.main.io;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResponse {
	private String userId;
	private String name;
	private String email;
	private String imgUrl;
	private LocalDateTime createdAt;
}
