package user.study.member.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import user.study.member.domain.user.Role;
import user.study.member.domain.user.User;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class FormUser {
    @NotEmpty(message = "required user name")
    private String name;
    @NotEmpty(message = "required user password")
    private String pwd;
    private Role role = Role.USER;

    //   DTO 를 Entity 로 변환
    public User toEntity() {
        User user = User.formBuilder()
                .name(this.name)
                .pwd(this.pwd)
                .role(this.role)
                .build();
        return user;
    }
}
