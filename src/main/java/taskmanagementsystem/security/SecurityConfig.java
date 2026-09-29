package taskmanagementsystem.security;
import java.util.List;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import taskmanagementsystem.repository.UserRepository;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
 @Bean UserDetailsService userDetailsService(UserRepository users) {
  return username -> users.findByUsername(username).map(u ->
   org.springframework.security.core.userdetails.User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole()).build())
   .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
 }
 @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  return http.cors(Customizer.withDefaults()).csrf(c -> c.disable())
   .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a -> a.requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll().anyRequest().authenticated())
   .httpBasic(b -> b.authenticationEntryPoint((request,response,exception) -> response.sendError(401)))
   .build();
 }
 @Bean CorsConfigurationSource corsConfigurationSource() {
  CorsConfiguration c = new CorsConfiguration();
  c.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
  c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
  c.setAllowedHeaders(List.of("Content-Type","Authorization"));
  UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/api/**",c); return source;
 }
}
