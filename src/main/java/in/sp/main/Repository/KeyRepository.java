package in.sp.main.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import in.sp.main.entity.UserKey;
public interface KeyRepository extends JpaRepository<UserKey,Long>{
	Optional<UserKey> findByUserId(String userId);

	List<UserKey> findByUserIdIn(List<String> userIds);
}
