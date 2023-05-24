package user.study.member.unused;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import user.study.member.domain.user.User;

import java.util.ArrayList;
import java.util.Collection;

// Security 의 Session 을 이용하려면 Authentication 타입 객체여야 하고,
// Authentication 안에 User 정보는 UserDetails 라는 인터페이스를 구현한 객체여야한다.
// 이 프로젝트에서는
// Authentication -> PrincipalDetailsService 클래스로 UserDetailsService 인터페이스 구현
// UserDetails -> PrincipalDetails 클래스로 UserDetails 인터페이스 구현
@Getter
@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails {

    private final User user;

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
//        collection.add(()->{return user.getRoleKey();}) 로 줄이기 가능
        return collection;
    }

    @Override
    public String getPassword() {
        String pwd = user.getPwd();
        System.out.println("pwd : " + pwd);
        return pwd;
    }

    @Override
    public String getUsername() {
        String username = user.getName();
        System.out.println("user : " + username);
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

}
