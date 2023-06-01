package user.study.member.domain.token;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

// ToDo: Refresh Token 을 쓰는 이유는 DB 에서 사용자 정보를 꺼내지 않기 위함! 그러므로
//      redis 저장소에 저장할 수 있도록 바꾸자
@Entity
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id @GeneratedValue
    private Long id;
    
    private String refreshToken;

    private Date refreshTokenExpirationTime;

    @Builder
    public RefreshToken(String refreshToken, Date refreshTokenExpirationTime){
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }
}
