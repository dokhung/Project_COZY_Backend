package com.ohgiraffers.COZYbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class COZYApplication {

    public static void main(String[] args) {
        SpringApplication.run(COZYApplication.class, args);
        System.out.println("협업툴 프로젝트의 백엔드 서버가 실행이 정상적으로 되었습니다.");
        System.out.println("스웨거UI의 주소는 http://localhost:8000/swagger-ui/index.html 입니다.");
    }

}
