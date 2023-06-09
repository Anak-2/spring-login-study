package user.study.member.domain.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.Assert;

import java.sql.Timestamp;

@Getter
@Slf4j
@Setter
@Entity
@NoArgsConstructor
@ToString
public class User{
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String pwd;

//    OAuth login을 위한 필드
    private String email;
    private String picture;

    private String provider;
    private String providerId;

    @CreationTimestamp
    private Timestamp createDate;
//    @Builder.Default --> NoArgsConstructor 와 충돌
    @Enumerated(EnumType.STRING)
    private Role role = Role.GUEST;

//    OAuth 가입
    @Builder(builderClassName = "oauthBuilder", builderMethodName = "oauthBuilder")
    public User(String name, String email, Role role, String provider, String providerId) {
        log.debug("Call oauthBuilder name = {} email = {}",name,email);
        this.name = name;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

//    Form 가입
    @Builder(builderClassName = "formBuilder", builderMethodName = "formBuilder")
    public User(String name, String pwd, Role role){
//        Assert.hasText("name","name null");
//        Assert.hasText("pwd","pwd null");
        log.debug("Call formBuilder name = {} pwd = {} role = {}",name,pwd,role);
        this.name = name;
        this.pwd = pwd;
        this.role = role;
    }

    public User update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }
}
