package jwc.com.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PostBox {
    @Id
    @GeneratedValue
    @Column(name="postbox_id")
    private Long id;

    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    private CompanyCode companyCode;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @Builder
    public PostBox(String invoiceNumber, CompanyCode companyCode, Member member)
    {
        this.invoiceNumber=invoiceNumber;
        this.companyCode=companyCode;
        this.member=member;

    }
}
