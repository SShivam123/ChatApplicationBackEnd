package in.sp.main.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import in.sp.main.entity.Message;
import in.sp.main.entity.User;

public interface MessageRepository extends JpaRepository<Message,Long>{
	@Modifying
	@Query("UPDATE Message m SET m.user = null WHERE m.user.userId = :userId")
	void setUserToNull(String userId);

	
	
}
