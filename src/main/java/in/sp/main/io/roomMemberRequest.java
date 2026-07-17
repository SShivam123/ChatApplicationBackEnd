package in.sp.main.io;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class roomMemberRequest {
	@NotNull(message = "Email is required")
	@Email(message = "Enter please valid email") 
	private String email;
	private String roomId;
}
