package jwc.com.entity;

import javax.persistence.*;

@Entity
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
}
