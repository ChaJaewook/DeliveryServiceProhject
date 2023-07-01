package jwc.com.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id", updatable = false)
    private Long id;

    private String name;

    private String email;

    private String password;

    @OneToMany(mappedBy = "member")
    private List<PostBox> postBox=new ArrayList<>();

    @Builder
    public Member(String name, String email, String password)
    {
       this.name=name;
       this.email=email;
       this.password=password;
    }

}
