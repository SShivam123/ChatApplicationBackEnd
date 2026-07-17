package in.sp.main.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "userKey_tbl")
public class UserKey {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(columnDefinition = "LONGTEXT")
	private String publicKey;
	private LocalDateTime generatedAt;
	private String userId;
	@Column(columnDefinition = "LONGTEXT")
	private String enceyptedprivateKey;
	@Column(columnDefinition = "LONGTEXT")
	private String iv;
	@Column(columnDefinition = "LONGTEXT")
	private String salt;
}
