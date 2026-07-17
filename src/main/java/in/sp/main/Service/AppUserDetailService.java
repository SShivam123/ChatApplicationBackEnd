package in.sp.main.Service;
import java.util.ArrayList;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import in.sp.main.Repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService{
	private final UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		in.sp.main.entity.User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found in this email"+email));
		return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
		
	}

}
