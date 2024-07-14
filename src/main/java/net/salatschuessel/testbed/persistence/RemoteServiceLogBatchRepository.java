package net.salatschuessel.testbed.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import net.salatschuessel.testbed.model.RemoteServiceLog;

@Repository
public class RemoteServiceLogBatchRepository {

	private final SimpleJdbcInsert simpleJdbcInsert;

	@Autowired
	public RemoteServiceLogBatchRepository(final JdbcTemplate jdbcTemplate) {
		this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("remote_service_log")
				.usingGeneratedKeyColumns("id")
				.usingColumns("direction", "body", "insert_time")
//				.withoutTableColumnMetaDataAccess()
		;
	}

	public void insert(final List<RemoteServiceLog> logs) {
// 1.
		this.simpleJdbcInsert.executeBatch(SqlParameterSourceUtils.createBatch(logs));

// 2.
//		this.simpleJdbcInsert.executeBatch(this.buildMapArray(logs));

	}

	private Map<String, ?>[] buildMapArray(final List<RemoteServiceLog> logs) {
		@SuppressWarnings("unchecked")
		final Map<String, ?>[] array = new HashMap[logs.size()];
		int i = 0;
		for (final var log : logs) {
			final Map<String, Object> map = new HashMap<>();
			map.put("direction", log.direction());
			map.put("body", log.body());
			map.put("insert_time", log.insertTime());
			array[i++] = map;
		}
		return array;
	}
}
