package net.salatschuessel.testbed.persistence;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import net.salatschuessel.testbed.model.RemoteServiceLog;

@Repository
public class RemoteServiceLogRepository {

	private final JdbcClient jdbcClient;
	private static final String INSERT_SQL = "INSERT INTO remote_service_log (direction, body, insert_time) VALUES (:direction, :body, :insert_time)";
	private static final String SELECT_SQL = "SELECT direction, body, insert_time FROM remote_service_log WHERE id = :id";
	private static final String SELECT_SQL_MAX_ID = "SELECT MAX(id) AS id FROM remote_service_log";
	private final RowMapper<RemoteServiceLog> rowMapper = new DataClassRowMapper<>(RemoteServiceLog.class);

	@Autowired
	public RemoteServiceLogRepository(final JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public void insert(final List<RemoteServiceLog> logs) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();

		logs.forEach(log -> this.jdbcClient.sql(INSERT_SQL)
				.param("direction", log.direction())
				.param("body", log.body())
				.param("insert_time", log.insertTime())
				.update(keyHolder));
	}

	public BigInteger selectMaxId() {
		return this.jdbcClient.sql(SELECT_SQL_MAX_ID)
				.query(BigInteger.class)
				.single();
	}

	public RemoteServiceLog selectOwnRowMapper(final BigInteger id) {
		return this.jdbcClient.sql(SELECT_SQL)
				.param("id", id)
				.query(ROW_MAPPER)
				.single();
	}

	public RemoteServiceLog selectGenericRowMapper(final BigInteger id) {
		return this.jdbcClient.sql(SELECT_SQL)
				.param("id", id)
				.query(this.rowMapper)
				.single();
	}

	private static final RowMapper<RemoteServiceLog> ROW_MAPPER = (final ResultSet rs, final int rowNum) -> {
		return new RemoteServiceLog(rs.getString("direction"), rs.getString("body"),
				rs.getObject("insert_time", Instant.class));
	};
}
