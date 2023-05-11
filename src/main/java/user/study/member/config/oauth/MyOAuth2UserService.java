package user.study.member.config.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/*
* 구글 로그인 유저를 처리할 함수 (config.auth.CustomOAuth2UserService.class 로 대체 가능)
* 
* 구글 로그인 버튼 -> 구글 로그인 창 -> 로그인 완료하면 code를 리턴 (OAuth-Client 라이브러리가 처리)
* -> AccessToken 요청
*
* userRequest 정보 -> loadUser() 로 회원 프로필 받음
* */
@Service
public class MyOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getAccessToken: "+userRequest.getAccessToken());
        System.out.println("getClientRegistration : "+userRequest.getClientRegistration());
        System.out.println("getAdditionalParameter : "+userRequest.getAdditionalParameters());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User.getName() : "+oAuth2User.getName());
        System.out.println("oAuth2User.getAuthorities() : "+oAuth2User.getAuthorities());
        // 실질적으로 필요한 것
        System.out.println("oAuth2User.getAttributes() : "+oAuth2User.getAttributes());

        return oAuth2User;
    }
}
