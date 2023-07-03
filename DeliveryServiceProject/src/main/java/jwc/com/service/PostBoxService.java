package jwc.com.service;

import jwc.com.entity.PostBox;
import jwc.com.repository.PostBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostBoxService {
    private final PostBoxRepository postBoxRepository;

    public List<PostBox> getAllPostBoxes()
    {
        List<PostBox> postBoxes=postBoxRepository.findAll();
        return postBoxes;
    }

    public Optional<PostBox> getPostBoxById(Long id)
    {
        Optional<PostBox> findItem=postBoxRepository.findById(id);
        return findItem;
    }

    @Transactional
    public Long savePostBox(PostBox item, Long memberId)
    {
        postBoxRepository.save(item);
        return item.getId();
    }

    public Long deletePostBox(PostBox item)
    {
        postBoxRepository.delete(item);
        return item.getId();
    }
    

}
