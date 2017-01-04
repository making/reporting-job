package com.example;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JobEvent implements Serializable {
	private String jobEventId;
	@JsonIgnore
	private String jobRequestId;
	private JobStatus jobStatus;
	private Instant createdAt;

	public JobEvent(String jobEventId, String jobRequestId, JobStatus jobStatus,
			Instant createdAt) {
		this.jobEventId = jobEventId;
		this.jobRequestId = jobRequestId;
		this.jobStatus = jobStatus;
		this.createdAt = createdAt;
	}

	public JobEvent() {
	}

	public String getJobEventId() {
		return jobEventId;
	}

	public void setJobEventId(String jobEventId) {
		this.jobEventId = jobEventId;
	}

	public String getJobRequestId() {
		return jobRequestId;
	}

	public void setJobRequestId(String jobRequestId) {
		this.jobRequestId = jobRequestId;
	}

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "JobEvent{" + "jobEventId='" + jobEventId + '\'' + ", jobRequestId='"
				+ jobRequestId + '\'' + ", jobStatus=" + jobStatus + ", createdAt="
				+ createdAt + '}';
	}
}
