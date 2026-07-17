package in.sp.main.io;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RestPasswordRequest {
	@NotNull(message = "token is required")
	private String token;
	@NotNull(message = "password is required")
	@Size(min = 6, message = "Password character should be 6")
	private String password;
	private String encryptedPrivateKey;
	private String iv;
	private String salt;
	private String publicKey;
}
