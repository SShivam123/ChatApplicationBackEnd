package in.sp.main.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "message_table")
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	private String content;
	private String iv;
	@CreationTimestamp
	@Column(nullable = false,updatable = false)
	private LocalDateTime sentAt;
	@ManyToOne()
	@JoinColumn(name = "room_id",nullable = false)
	private Room room;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = true)
	private User user;
	private String type;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
	private String imageUrl;
    
    @OneToMany(mappedBy = "message",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MessageKey> messageKeys = new ArrayList<MessageKey>();
}
