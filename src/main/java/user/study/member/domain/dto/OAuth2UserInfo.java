package user.study.member.domain.dto;

import user.study.member.domain.user.Role;
import user.study.member.domain.user.User;

import java.util.*;

public class OAuth2UserInfo {

    private Map<String, Object> attributes;
    private String platform;

    public OAuth2UserInfo(Map<String, Object> attributes, String platform){
        this.attributes = attributes;
        this.platform = platform;
    }

    public String getProviderId(){
        if(platform.equals("google")){
            return (String) attributes.get("sub");
        }
//        카카오 id가 Long 타입이기 때문에 (String) 타입 캐스팅 실패
//        String.valueOf() 로 수정
        return String.valueOf(attributes.get("id"));
    }

    public String getName(){
        return (String) attributes.get("name");
    }

    public String getEmail(){
        return (String) attributes.get("email");
    }

    public String getPlatform(){
        return platform;
    }

    public User toEntity(){
        User user = User.oauthBuilder()
                .name(platform+"_"+this.getProviderId()) // -> google_PK
                .email(this.getEmail())
                .role(Role.USER)
                .provider(platform)
                .providerId(this.getProviderId())
                .build();
        return user;
    }
}
