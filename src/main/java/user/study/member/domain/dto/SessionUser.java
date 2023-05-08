package user.study.member.domain.dto;

import lombok.Getter;
import user.study.member.domain.user.User;

import java.io.Serializable;

// 데이터를 외부 서버로 전송하기 위해서 객체를 바이트 단위로 변환 필요 --> Serializable 구현한 이유
// 객체를 DB에 저장하거나 파일로 저장한 객체를 전송하는 등의 상황에선 스트림을 통해 데이터를 보내는데,
// 이때 바이트 단위로 보내야한다
@Getter
public class SessionUser implements Serializable {
//    transient 키워드가 붙은 필드는 직렬화에서 제외된다.
//    private transient int field;
    private String name;
    private String email;

    public SessionUser(User user){
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
