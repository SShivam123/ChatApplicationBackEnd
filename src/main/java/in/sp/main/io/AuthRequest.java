package in.sp.main.io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
	@NotNull(message = "Email is required")
	@Email(message = "please enter valid email")
	private String email;
	@NotNull(message = "password is required")
	private String password;
}
