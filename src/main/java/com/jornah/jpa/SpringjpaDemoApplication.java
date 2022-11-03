package com.jornah.jpa;

import com.jornah.jpa.dao.UserRepo;
import com.jornah.jpa.model.User;
import com.jornah.jpa.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringjpaDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringjpaDemoApplication.class, args);
        test1(context);

    }

    private static void test1(ApplicationContext  context) {
        UserService userService = context.getBean(UserService.class);
//        User userInfo = userService.getUserInfo(1L);
//        userService.test();
//
//        UserRepo userRepo = context.getBean(UserRepo.class);
////        List<User> users = userRepo.findAllBy();
        userService.queryBySpecificationBuilder();


    }

}
