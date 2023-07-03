package jwc.com.dto;

import jwc.com.entity.CompanyCode;
import jwc.com.entity.Member;
import jwc.com.entity.PostBox;
import lombok.Builder;

public class PostBoxRequest {
    private String invoiceNumber;
    private CompanyCode companyCode;
    private Member member;

    private Long memberId;

    @Builder
    public PostBox toEntity()
    {
        return PostBox
                .builder()
                .invoiceNumber(invoiceNumber)
                .companyCode(companyCode)
                .member(member)
                .build();
    }
}
