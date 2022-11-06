package com.jornah.jpa.service;

import com.jornah.jpa.dao.UserRepo;
import com.jornah.jpa.model.User;
import com.jornah.jpa.model.UserDto;
import com.jornah.jpa.util.JpaQueryRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author licong
 * @date 2022/10/22 23:34
 */
@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public User getUserInfo(Long id) {
//        User user = userRepo.findById(id).orElse(null);
        List<User> all = userRepo.findAll();
        System.out.println(all.size());
//        all.get(0).getPostList().get(0);
//        all.get(1).getPostList().get(0);
//        System.out.println(JSONObject.toJSONString(user));
        return null;
    }

    public void saveUserInfo() {
        List<User> userList = IntStream.range(0, 5).mapToObj(i -> {
            User user = new User();
            user.setEmail("email" + i);
            user.setUsername("username" + i);
            user.setName("name" + i);
            return user;
        }).collect(Collectors.toList());
        userRepo.saveAll(userList);
    }

    public void test() {
        System.out.println(1);
        System.out.println(2);
        System.out.println(3);
        System.out.println(99999);
        System.out.println(99999);
        System.out.println(99999);
        System.out.println(4);
    }

    public void queryBySpecificationBuilder(Long userid) {
        JpaQueryRunner<User, UserDto> jpaQueryRunner = new JpaQueryRunner<>(entityManager, User.class, UserDto.class);
        List<UserDto> result = jpaQueryRunner
                .select(User::getUsername, User::getId)
                .eq(Objects.nonNull(userid),User::getId, userid)
                .executeQuery();
        result.forEach(System.out::println);

    }

}
