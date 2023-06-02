package user.study.member.global.httpStatus;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum CustomHttpStatus {
    REFRESH_ACCESS_TOKEN(260, "Refresh Access Token with Refresh Token");

    private final int value;
    private final String reasonPhrase;

    private CustomHttpStatus(int value, String reasonPhrase){
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }
}
