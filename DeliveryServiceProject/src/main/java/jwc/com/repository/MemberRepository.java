package jwc.com.repository;

import jwc.com.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
