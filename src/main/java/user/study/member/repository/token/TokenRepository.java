package user.study.member.repository.token;

import org.springframework.data.jpa.repository.JpaRepository;
import user.study.member.domain.token.RefreshToken;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken);
    boolean deleteRefreshTokenByRefreshToken(String refreshToken);
}
