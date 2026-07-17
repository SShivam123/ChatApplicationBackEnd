package in.sp.main.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import in.sp.main.entity.Room;

public interface roomRepository extends JpaRepository<Room, Long>{
	Optional<Room> findByRoomId(String roomId);
	
	boolean existsByRoomId(String roomId);
}
