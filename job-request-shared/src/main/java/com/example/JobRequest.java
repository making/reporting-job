package com.example;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import org.springframework.util.Assert;

public class JobRequest implements Serializable {
	private String jobRequestId;
	private String jobId;
	private List<JobEvent> jobEvents;
	private Instant createdAt;

	public JobRequest() {
	}

	JobRequest(String jobRequestId, String jobId) {
		this.jobRequestId = jobRequestId;
		this.jobId = jobId;
	}

	public JobRequest(String jobRequestId, String jobId, List<JobEvent> jobEvents,
			Instant createdAt) {
		this.jobRequestId = jobRequestId;
		this.jobId = jobId;
		this.jobEvents = jobEvents;
		this.createdAt = createdAt;
	}

	public String getJobRequestId() {
		return jobRequestId;
	}

	public void setJobRequestId(String jobRequestId) {
		this.jobRequestId = jobRequestId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public List<JobEvent> getJobEvents() {
		return jobEvents;
	}

	public void setJobEvents(List<JobEvent> jobEvents) {
		this.jobEvents = jobEvents;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "JobRequest{" + "jobRequestId='" + jobRequestId + '\'' + ", jobId='"
				+ jobId + '\'' + ", jobEvents=" + jobEvents + ", createdAt=" + createdAt
				+ '}';
	}

	public JobRequest copyOnlyIds() {
		Assert.notNull(jobRequestId, "jobRequestId must not be null.");
		Assert.notNull(jobId, "jobId must not be null.");
		return new JobRequest(jobRequestId, jobId);
	}
}
