package user.study.member.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.study.member.domain.token.RefreshToken;
import user.study.member.repository.token.TokenRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public boolean addToken(String refreshToken, Date refreshTokenExpirationTime){
        RefreshToken rt = RefreshToken.builder().
                refreshToken(refreshToken)
                .refreshTokenExpirationTime(refreshTokenExpirationTime)
                .build();
        tokenRepository.save(rt);
        return true;
    }
    public boolean removeToken(String refreshToken){
        RefreshToken rt = tokenRepository.findRefreshTokenByRefreshToken(refreshToken).orElse(null);
        if(rt != null){
            tokenRepository.deleteRefreshTokenByRefreshToken(rt.getRefreshToken());
            return true;
        }
        return false;
    }
}
