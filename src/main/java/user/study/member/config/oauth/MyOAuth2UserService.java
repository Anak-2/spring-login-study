package user.study.member.config.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import user.study.member.domain.user.Role;
import user.study.member.domain.user.User;
import user.study.member.repository.UserJpaRepository;
import user.study.member.repository.UserRepository;
import java.util.*;

/*
* OAuth 로그인 유저를 처리할 함수 (config.auth.CustomOAuth2UserService.class 로 대체 가능)
* 
* 구글 로그인 버튼 -> 구글 로그인 창 -> 로그인 완료하면 code를 리턴 (OAuth-Client 라이브러리가 처리)
* -> AccessToken 요청
*
* userRequest 정보 -> loadUser() 로 회원 프로필 받음
* */
@Service("myOauth2UserService")
public class MyOAuth2UserService extends DefaultOAuth2UserService {

//    과제: UserRepository, UserJpaRepository 를 DI 할 수 있도록 하나로 합치기
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 실질적으로 필요한 것
        System.out.println("oAuth2User.getAttributes() : "+oAuth2User.getAttributes());

        return processOAuth2User(userRequest,oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User){
//        과제: DTO (OAuthAttributes) 에 받아서 저장할 때 Entity로 넘기도록 리팩토링
        User user; // 정보를 담을 Entity

        Map<String, Object> attributes = oAuth2User.getAttributes();
//        OAuth 를 제공하는 서버 (여기선 google)
        String provider = userRequest.getClientRegistration().getClientId();
//        사용자의 Id (Unique 보장)
        String providerId = (String) attributes.get("sub");
//        사용자의 이름
        String name = (String) attributes.get("name");
//        사용자의 이메일
        String email = (String) attributes.get("email");
//        역할은 USER 로 지정
        Role role = Role.USER;

        user = User.oauthBuilder()
                .name(provider+"_"+providerId) // -> google_PK
                .email(email)
                .role(role)
                .provider(provider)
                .providerId(providerId)
                .build();

        Optional<User> findUser = userJpaRepository.findByProviderAndProviderId(provider, providerId);
//        구글로 가입한 회원이 없으면 자동 가입해주기
        if(!findUser.isPresent()){
            userJpaRepository.save(user);
        }else{
//            가입한 회원이 있으면 업데이트만
            User updateUser = findUser.get();
            updateUser.setEmail(email);
            userJpaRepository.save(updateUser);
        }

        return new PrincipalDetails2(user,attributes);
    }
}
