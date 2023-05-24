package user.study.member.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

public class UserRequestDto {
    @Getter
    @Setter
    public static class LoginDTO{
        @NotEmpty
        private String name;
        @NotEmpty
        private String pwd;
    }
}
