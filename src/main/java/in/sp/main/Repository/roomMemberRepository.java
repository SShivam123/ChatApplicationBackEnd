package in.sp.main.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.sp.main.entity.Room;
import in.sp.main.entity.roomMembers;
import in.sp.main.io.MemberResponseForUsers;

public interface roomMemberRepository extends JpaRepository<roomMembers,Long>{

	boolean existsByRoomIdAndEmail(String roomId, String email);

	boolean existsByRoomIdAndUserId(String roomId, String email);

	boolean existsByEmailAndRoleAndRoomId(String email, String string, String roomId);

	boolean existsByEmailAndRole(String email, String searchEmail);
	
	@Query("select r from Room r where r.roomId in(select rm.roomId from roomMembers rm where rm.userId = :userId)")
	List<Room> findByUserId(@Param("userId") String userId);

	Integer countByRoomId(String roomId);
	
	@Query("""
		    select new in.sp.main.io.MemberResponseForUsers(
		        u.name,
		        rm.email,
		        rm.roomId,
		        rm.userId
		        
		    )
		    from roomMembers rm
		    join User u on u.userId = rm.userId
		    where rm.role='USER' and rm.roomId in (
		        select r.roomId
		        from Room r
		        where r.createdBy.userId = :userId
		    )
		""")
	List<MemberResponseForUsers> findAllUsersRooms(@Param("userId")String userId);

	void deleteByUserIdAndRoomId(String userId, String roomId);

	void deleteAllByRoomId(String roomId);

	@Query("SELECT rm from roomMembers rm WHERE rm.roomId = :roomId and rm.userId != :userId order By rm.joinedAt ASC")
	List<roomMembers> findOtherMember(String userId, String roomId);

	void deleteAllByUserId(String userId);

	List<roomMembers> findAllByRoomId(String roomId);

}
