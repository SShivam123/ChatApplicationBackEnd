package in.sp.main.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sp.main.entity.MessageKey;

public interface MessageKeyRepository extends JpaRepository<MessageKey, Long>{
	
}
