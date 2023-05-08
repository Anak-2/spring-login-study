package user.study.member.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Getter
@Slf4j
@Setter
@Entity
@NoArgsConstructor
public class User{
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String pwd;

//    OAuth login 에 필요
    private String email;
    private String picture;

//    @Builder.Default --> NoArgsConstructor 와 충돌
    @Enumerated(EnumType.STRING)
    private Role role = Role.GUEST;

//    Google 가입
    @Builder(builderClassName = "oauthBuilder", builderMethodName = "oauthBuilder")
    public User(String name, String email, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

//    Form 가입
    @Builder(builderClassName = "formBuilder", builderMethodName = "formBuilder")
    public User(String name, String pwd, Role role){
//        Assert.hasText("name","name null");
//        Assert.hasText("pwd","pwd null");
        log.debug("builder name = {} pwd = {} role = {}",name,pwd,role);
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
