package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
class MemberRepositoryV2Test {

	MemberRepositoryV1 repository;

	//각 테스트가 실행 되기 직전에 한번 실행 되는 것.
	@BeforeEach
	void beforeEach() {
		// 기본 DriverManager 는 항상 새로운 커넥션을 획득
//		DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

		// 커넥션 풀링
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);

		repository = new MemberRepositoryV1(dataSource);
	}

	@Test
	void crud() throws SQLException {
		// save
		Member member = new Member("memberVO5", 10000);
		repository.save(member);

		// findById
		Member findMember = repository.findById(member.getMemberId());
		log.info("findMember={}", findMember);
		assertThat(findMember).isEqualTo(member); // 객체가 달라도 true가 나오는 이유는 Member의 @Data 에서 equals And Hashcode로 값들이 같은지 비교하기 때문.

		// update : money 10000 -> 20000
		repository.update(member.getMemberId(), 20000);
		Member updateMember = repository.findById(member.getMemberId());
		assertThat(updateMember.getMoney()).isEqualTo(20000);

		// delete
		repository.delete(member.getMemberId());
		assertThatThrownBy(() -> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);

	}
}