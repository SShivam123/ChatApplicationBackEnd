package in.sp.main.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.sp.main.entity.User;
import in.sp.main.io.roomMemberResponse;

public interface UserRepository extends JpaRepository<User, Long>{

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findByUserId(String userId);

	List<User> findByEmailContainingIgnoreCase(String name);

	List<User> findByUserIdIn(List<String> memberIds);
	
}
