package in.sp.main.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tbl")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String userId;
	private String name;
	private String email;
	private String password;
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String imgUrl; // Ab isme path nahi, poori image ka text save hoga
	
	@OneToMany(mappedBy = "createdBy")
	@JsonIgnore
	private List<Room> rooms = new ArrayList<Room>();
	
	@OneToMany(mappedBy = "user")
	private List<Message> messages = new ArrayList<Message>();
}
