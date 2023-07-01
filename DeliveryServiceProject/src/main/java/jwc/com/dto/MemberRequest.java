package jwc.com.dto;

import jwc.com.entity.Member;

public class MemberRequest {

    private String name;
    private String email;
    private String password;

    public Member toEntity(){
        return Member.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
    }

}
