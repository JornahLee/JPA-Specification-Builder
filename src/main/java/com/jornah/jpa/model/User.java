package com.jornah.jpa.model;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author licong
 * @date 2022/10/11 21:53
 */
@Data
@Entity
@Table(name = "users")
//@NamedEntityGraph(
//        name = "user.all",
//        attributeNodes = {
//                @NamedAttributeNode("postList")
//        }
//)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String username;
    String name;
    String email;
    @ColumnDefault("CURRENT_TIMESTAMP()")
    @Column(nullable = true)
    LocalDateTime updated;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "user")
    List<Post> postList;
}
