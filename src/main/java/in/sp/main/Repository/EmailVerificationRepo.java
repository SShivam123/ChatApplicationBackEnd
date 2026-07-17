package in.sp.main.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import in.sp.main.entity.EmailVerification;

public interface EmailVerificationRepo extends JpaRepository<EmailVerification, String>{

	Optional<EmailVerification> findByEmail(String string);

}
