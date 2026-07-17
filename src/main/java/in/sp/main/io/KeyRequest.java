package in.sp.main.io;
import java.time.LocalDateTime;
import in.sp.main.entity.UserKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KeyRequest {
	private String publicKey;
}
