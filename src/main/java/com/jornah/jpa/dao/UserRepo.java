package com.jornah.jpa.dao;

import com.jornah.jpa.model.User;
import com.jornah.jpa.model.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author licong
 * @date 2022/10/11 21:51
 */
//@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    @Query(value = "select new com.jornah.jpa.model.UserDto(u.username,u.id,u.updated) from User u")
    List<UserDto> findUser();

    @Query(nativeQuery = true,value = "select username,count(*) from users group by username ")
    Page<Object[]> aggs(Pageable pageable);

//    @EntityGraph(value = "user.all",type= EntityGraph.EntityGraphType.FETCH)
//    List<User> findAllBy();



}
