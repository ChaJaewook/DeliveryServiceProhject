package jwc.com.service;

import jwc.com.entity.Member;
import jwc.com.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final MemberRepository memberRepository;

    public List<Member> getAllMembers()
    {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id){
        return memberRepository.getReferenceById(id);
    }

    @Transactional
    public Long join(Member member)
    {
        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    public Long delete(Member member)
    {
        memberRepository.delete(member);
        return member.getId();
    }

}
