package jwc.com.controller;

import jwc.com.dto.MemberRequest;
import jwc.com.entity.Member;
import jwc.com.repository.MemberRepository;
import jwc.com.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<Member> save(@RequestBody MemberRequest request)
    {
        Member savedMember=memberService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedMember);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Member> list(@PathVariable("id") Long id)
    {
        Member member=memberService.getMemberById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(member);
    }
}
