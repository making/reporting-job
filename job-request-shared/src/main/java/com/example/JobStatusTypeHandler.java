package com.example;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class JobStatusTypeHandler extends BaseTypeHandler<JobStatus> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, JobStatus parameter,
			JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter.statusCode());
	}

	@Override
	public JobStatus getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return JobStatus.valueOf(rs.getInt(columnName));
	}

	@Override
	public JobStatus getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return JobStatus.valueOf(rs.getInt(columnIndex));
	}

	@Override
	public JobStatus getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return JobStatus.valueOf(cs.getInt(columnIndex));
	}
}
