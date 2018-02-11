package basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


/*
	https://auth0.com/blog/securing-spring-boot-with-jwts/
	https://github.com/auth0-blog/spring-boot-jwts
	https://github.com/szerhusenBC/jwt-spring-security-demo
*/
	
@SpringBootApplication
@RestController
public class Application {

    @GetMapping("/api/protected")
    public @ResponseBody Object hellWorld() {
        return "Hello World! This is a protected api";
    }

    @PostMapping("/login")
    public Object login(HttpServletResponse response, @RequestBody final Account account) throws IOException {
        if(isValidPassword(account)) {
            String jwt = JwtUtil.generateToken(account.username);
            return new HashMap<String,String>(){{
                put("token", jwt);
            }};
        }else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        //我们只对/api 开始的api检查jwt, 这里是为了让login能正常工作, 否则login也需要jwt
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                "/api/**");
        registrationBean.setFilter(filter);
        return registrationBean;
    }

    private boolean isValidPassword(Account ac) {
        return "admin".equals(ac.username)
                && "admin".equals(ac.password);
    }


    public static class Account {
        public String username;
        public String password;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}