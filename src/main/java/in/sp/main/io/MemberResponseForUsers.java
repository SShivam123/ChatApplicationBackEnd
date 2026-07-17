package in.sp.main.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseForUsers {
	private String name;
	private String email;
	private String roomId;
	private String userId;
}
