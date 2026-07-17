package in.sp.main.io;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChangePasswordRequest {
	@NotNull
	@Pattern(regexp = "^\\S+$", message = "Old password can not contain space")
	private String oldPassword;
	@NotNull
	@Pattern(regexp = "^\\S+$", message = "New password can not contain space")
	@Size(min = 6,message = "New password must be 6 character")
    private String newPassword;
	private String encryptedKey;
	private String iv;
	private String salt;
}
