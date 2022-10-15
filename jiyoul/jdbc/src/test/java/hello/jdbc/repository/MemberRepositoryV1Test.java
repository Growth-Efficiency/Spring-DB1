package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
class MemberRepositoryV1Test {

	MemberRepositoryV1 repositoryVO = new MemberRepositoryV1();

	@Test
	void crud() throws SQLException {
		// save
		Member member = new Member("memberVO5", 10000);
		repositoryVO.save(member);

		// findById
		Member findMember = repositoryVO.findById(member.getMemberId());
		log.info("findMember={}", findMember);
		assertThat(findMember).isEqualTo(member); // 객체가 달라도 true가 나오는 이유는 Member의 @Data 에서 equals And Hashcode로 값들이 같은지 비교하기 때문.

		// update : money 10000 -> 20000
		repositoryVO.update(member.getMemberId(), 20000);
		Member updateMember = repositoryVO.findById(member.getMemberId());
		assertThat(updateMember.getMoney()).isEqualTo(20000);

		// delete
		repositoryVO.delete(member.getMemberId());
		assertThatThrownBy(() -> repositoryVO.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);

	}
}