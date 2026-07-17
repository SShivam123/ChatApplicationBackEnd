package in.sp.main.io;
import java.time.LocalDateTime;
import java.util.List;
import in.sp.main.entity.Message;
import in.sp.main.entity.MessageKey;
import in.sp.main.entity.Room;
import in.sp.main.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKeyResponse {
	private String userId;
	private String publicKey;
}
