package in.sp.main.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import in.sp.main.entity.User;
import in.sp.main.entity.UserKey;

public interface UserKeyRepository extends JpaRepository<UserKey, Long>{

	Optional<UserKey> findByUserId(String userId);

}
