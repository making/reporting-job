package com.example;

import java.io.Serializable;

public class JobRequest implements Serializable {
	private String jobRequestId;
	private String jobId;

	public JobRequest() {
	}

	public JobRequest(String jobRequestId, String jobId) {
		this.jobRequestId = jobRequestId;
		this.jobId = jobId;
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

	@Override
	public String toString() {
		return "JobRequest{" + "jobRequestId='" + jobRequestId + '\'' + ", jobId='"
				+ jobId + '\'' + '}';
	}
}
