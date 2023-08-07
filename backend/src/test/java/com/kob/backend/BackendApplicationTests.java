package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("a"));
        System.out.println(passwordEncoder.encode("123"));
        String encodedPasswod = passwordEncoder.encode("123");
        System.out.println(encodedPasswod);
        System.out.println(passwordEncoder.matches("123", "$2a$10$5s831w9lwlTZLk1Kk8Sta..rmfFUzuqLIuj9ol9BFBqJmdrgFWMbu"));
        System.out.println(passwordEncoder.matches("123", "$2a$10$YAWsYM4MoSotwtGaq.3h0.desuqDITEOPvWjqIKMQPnIoQBpZl9mO"));
    }

}
