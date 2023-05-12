package user.study.member.config.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import user.study.member.config.auth.PrincipalDetails;
import user.study.member.domain.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// Controller에서 Authentication 객체로 세션에서 가져올 때 유저가 무엇으로 로그인하는지 알 필요 없이
// UserDetails, OAuth2User 모두 구현한 클래스 PrincipalDetails2 만들기
@Getter
public class PrincipalDetails2 implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> attributes;

//    일반 시큐리티 로그인 생성자 (UserDetailsService 에서 사용)
    public PrincipalDetails2(User user){
        this.user = user;
    }
//    OAuth 로그인 생성자 (DefaultOAuth2UserService 에서 사용)
    public PrincipalDetails2(User user, Map<String, Object> attributes){
        this.user = user;
        this.attributes = attributes;
    }

    public User toEntity(){
        return this.user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    //    해당 User 의 권한(Autority)를 리턴하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRoleKey();
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        String pwd = user.getPwd();
        System.out.println("pwd : "+pwd);
        return pwd;
    }

    @Override
    public String getUsername() {
        String username = user.getName();
        System.out.println("user : "+username);
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 사용 예시) 1년 동안 우리 사이트에 로그인하지 않은 계정을 휴면 계정으로 바꾸는 메서드
//      if user.getLoginDate() - currentTime > 1년 :
//        return false;
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}