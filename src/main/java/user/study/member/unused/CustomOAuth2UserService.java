package user.study.member.unused;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import user.study.member.unused.OAuthAttributes;
import user.study.member.domain.dto.SessionUser;
import user.study.member.domain.user.User;
import user.study.member.repository.UserRepository;

import java.util.*;
/*
* 구글 로그인 이후 가져온 사용자의 정보를 기반으로 가입 및 정보 수정, 세션 저장 등의 기능
* 이 클래스는 클라이언트가 oauth 로그인을 했을 때 사용자 정보 처리를 loadUser를 통해 진행해서 OAuthAttributes DTO 로 받는다
* */
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    /*
    - 구글로 로그인한 사용자를 후처리하는 함수
*   @Params
*   registrationId - 현재 로그인 서비스가 네이버 로그인인지, 구글 로그인인지 구분하기 위해 사용
*   userNameAttributeName - OAuth2 로그인 진행 시 키가 되는 값, pk와 동일
*   OAuthAttributes - OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스(커스텀)
*   SessionUser - 세션에 사용자 정보를 저장하기 위한 DTO 클래스, User 클래스를 그대로 사용하다가 다른 엔티티와 관계가 생길 경우
*                   해당 자식들까지 직렬화 돼서 저장되기 때문에 성능 이슈 등이 발생할 수 있기 때문에
*                   직렬화 기능을 가진 세션 DTO 를 하나 추가로 만들었다
* */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug("getAttributes : {}", oAuth2User.getAttributes());

//        provider : Google
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        providerId : Google에서 해당 유저의 고유 Id
//        String providerId = oAuth2User.getAttribute("sub");
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

//    로그인 정보 있으면 가져오고 없으면 저장
    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
