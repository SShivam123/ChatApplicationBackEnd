package in.sp.main.filter;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import in.sp.main.Service.AppUserDetailService;
import in.sp.main.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final AppUserDetailService appUserDetailService;
	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String path = request.getServletPath();
		if(path.equals("/login")) {
			 path.startsWith("/uploads/" );
			filterChain.doFilter(request, response);
			return;
		}
		
		String authHeader = request.getHeader("Authorization");
		String email = null;
		String jwtToken = null;
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			jwtToken = authHeader.substring(7);
		}
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("jwt".equals(cookie.getName())) {
					jwtToken = cookie.getValue();
					email = jwtService.extractUserName(jwtToken);
					break;
				}
			}
		}

		if (jwtToken == null) {
			Cookie[] cookiee = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookiee) {
					if ("jwt".equals(cookie.getName())) {
						jwtToken = cookie.getValue();
						break;
					}
				}
			}
		}

		if (jwtToken != null) {
			email = jwtService.extractUserName(jwtToken);
			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = appUserDetailService.loadUserByUsername(email);
				if (jwtService.validToken(jwtToken, userDetails)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
		}
		filterChain.doFilter(request, response);
	}

}

