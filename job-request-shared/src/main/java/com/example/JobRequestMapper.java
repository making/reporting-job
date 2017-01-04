package com.example;

import java.util.List;

import org.apache.ibatis.annotations.*;

@Mapper
public interface JobRequestMapper {
	@Select("SELECT job_request_id, job_id, created_at FROM job_request WHERE job_request_id = #{jobRequestId}")
	@Results(@Result(property = "jobEvents", column = "job_request_id", many = @Many(select = "com.example.JobRequestMapper.findJobEventByJobRequestId")))
	JobRequest findByJobRequestId(@Param("jobRequestId") String jobRequestId);

	@Select("SELECT job_event_id, job_request_id, job_status, created_at FROM job_event WHERE job_request_id = #{job_request_id} ORDER BY created_at DESC")
	List<JobEvent> findJobEventByJobRequestId(@Param("jobRequestId") String jobRequestId);

	@Select("SELECT job_id FROM job_request WHERE job_request_id = #{jobRequestId}")
	String findJobIdByJobRequestId(@Param("jobRequestId") String jobRequestId);

	@Select("SELECT job_status FROM job_request AS jr INNER JOIN job_event AS je ON jr.job_request_id = je.job_request_id WHERE jr.job_request_id = #{jobRequestId} ORDER BY je.created_at DESC LIMIT 1")
	Integer findLatestJobStatusByJobRequestId(@Param("jobRequestId") String jobRequestId);

	@Insert("INSERT INTO job_request(job_request_id, job_id, created_at) VALUES(#{jobRequestId}, #{jobId}, #{createdAt})")
	void insertJobRequest(JobRequest jobRequest);

	@Insert("INSERT INTO job_event(job_event_id, job_request_id, job_status, created_at) VALUES (#{jobEventId}, #{jobRequestId}, #{jobStatus}, #{createdAt})")
	void insertJobEvent(JobEvent jobEvent);

	@Insert("INSERT INTO job_request_execution(job_request_id, job_execution_id) VALUES (#{jobRequestId}, #{jobExecutionId})")
	void insertJobExecution(@Param("jobRequestId") String jobRequestId,
			@Param("jobExecutionId") Long jobExecutionId);
}
