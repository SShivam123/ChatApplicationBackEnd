package in.sp.main.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import in.sp.main.Service.AppUserDetailService;
import in.sp.main.filter.JwtFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final AppUserDetailService appUserDetailService;
	private final JwtFilter jwtFilter;
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity security) {
		security.cors(Customizer.withDefaults());
		security.csrf(customizer->customizer.disable());
		security.authorizeHttpRequests(request->request.requestMatchers("/send-otp","/verify-otp","/forgot-password","/reset-password","/login","/register","/chat/**","/uploads/**").permitAll().anyRequest().authenticated());
		security.httpBasic(httpBasic->httpBasic.disable());
			security.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
			security.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class);
			return security.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider =new DaoAuthenticationProvider(appUserDetailService);
		provider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(provider);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() { 
		return new BCryptPasswordEncoder();
	}
}
