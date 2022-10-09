package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryVO {

	public Member save(Member member) throws SQLException {
		String sql = "insert into member(member_id, money) values (?, ?)";

		Connection connection = null;
		// Statement는 sql를 명령을 담을 수 있음.
		// PreparedStatement 는  sql에 파라미터를 바인딩 할 수 있음. (Statement를 상속 받았음)
		// SQL 인젝션 공격을 막기 위해선 PreparedStatement 를 꼭 쓰자.!!!!!!!!!
		PreparedStatement pstmt = null;

		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, member.getMemberId());
			pstmt.setInt(2, member.getMoney());
			pstmt.executeUpdate(); // Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다.
			return member;
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(connection, pstmt, null);
		}
	}

	public Member findById(String memberId) throws SQLException {
		String sql = "select * from member where member_id = ?";

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, memberId);

			rs = pstmt.executeQuery();
			if (rs.next()) { // PK로 조회할 거니까 무조건 1개아니면 0개라서. if문 씀.
				Member member = new Member();
				member.setMemberId(rs.getString("member_id"));
				member.setMoney(rs.getInt("money"));
				return member;
			} else {
				throw new NoSuchElementException("member not found memberId = " + memberId);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			close(connection, pstmt, rs);
		}
	}

	public void update(String memberId, int money) throws SQLException {
		String sql = "update member set money = ? where member_id=?";

		Connection connection = null;
		PreparedStatement pstmt = null;

		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, money);
			pstmt.setString(2, memberId);
			int resultSize = pstmt.executeUpdate();// Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다.
			log.info("resultSize = {}", resultSize);
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(connection, pstmt, null);
		}
	}


	public void delete(String memberId) throws SQLException {
		String sql = "delete from member where member_id=?";

		Connection connection = null;
		PreparedStatement pstmt = null;

		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, memberId);
			pstmt.executeUpdate();// Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다.
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(connection, pstmt, null);
		}
	}

	private void close(Connection con, Statement stmt, ResultSet rs) {

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.info("error", e);
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				log.info("error", e);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.info("error", e);
			}
		}
	}

	private Connection getConnection() {
		return DBConnectionUtil.getConnection();
	}
}
