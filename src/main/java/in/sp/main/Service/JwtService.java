package in.sp.main.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
	@Value("${jwt.secret.key}")
	private String secretkey;

		public String generateToken(String UserName) {
			Map<String,Object> claims = new HashMap<>();
			return Jwts.builder()
					.claims()
					.add(claims)
					.subject(UserName)
					.issuedAt(new Date(System.currentTimeMillis()))
					.expiration(new Date(System.currentTimeMillis() +1000 * 60 * 60 * 10))
					.and()
					.signWith(getkey())
					.compact();
		}

		private SecretKey getkey() {
			byte[] keybytes = Decoders.BASE64.decode(secretkey);
			return Keys.hmacShaKeyFor(keybytes);
		}

		public String extractUserName(String token) {
			return extractClaim(token,Claims::getSubject);
		}

		private <T> T extractClaim(String token,Function<Claims,T>claimResolver) {
			final Claims claims = extractAllClaims(token);
			return claimResolver.apply(claims);
		}

		private Claims extractAllClaims(String token) {
			return Jwts.parser()
					.verifyWith(getkey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
		}

		public boolean validToken(String token, UserDetails userDetails) {
			final String username= extractUserName(token);
			return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		}

		private boolean isTokenExpired(String token) {
			return extractExpiration(token).before(new Date());
		}

		private Date extractExpiration(String token) {
			return extractClaim(token,Claims::getExpiration);
		}

}
