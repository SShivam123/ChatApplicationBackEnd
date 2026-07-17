package in.sp.main.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import in.sp.main.entity.ResetPassword;

public interface ResetPasswordRepo extends JpaRepository<ResetPassword,Long>{

	Optional<ResetPassword> findByEmail(String email);

	Optional<ResetPassword> findByToken(String token);

}
