package jwc.com.controller;

import jwc.com.dto.PostBoxRequest;
import jwc.com.entity.Member;
import jwc.com.entity.PostBox;
import jwc.com.repository.PostBoxRepository;
import jwc.com.service.MemberService;
import jwc.com.service.PostBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostBoxController {
    private final PostBoxService postBoxService;
    private final MemberService memberService;

    @GetMapping("/add/{member_id}")
    public ResponseEntity<PostBox> save(@PathVariable("member_id") Long id, @RequestBody PostBoxRequest request)
    {
        postBoxService
    }

}
