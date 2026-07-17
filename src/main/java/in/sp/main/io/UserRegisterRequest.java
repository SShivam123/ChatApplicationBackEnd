package in.sp.main.io;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {
	@NotNull(message = "Name is required")
	private String name;
	@NotNull(message = "email is required")
	@Email(message = "please enter valid email")
	private String email;
	@NotNull(message = "password is required")
	@Size(min = 6,message = "password should be atleast 6 character")
	private String password;
	private String publicKey;
	private String encryptedPrivateKey;
	private String iv;
	private String salt;
}
