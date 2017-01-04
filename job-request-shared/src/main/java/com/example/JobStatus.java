package com.example;

import java.util.stream.Stream;

public enum JobStatus {
	REQUESTED(0), STARTED(1), FINISHED(2), ABORTED(9);

	private final int statusCode;

	JobStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	public int statusCode() {
		return statusCode;
	}

	public static JobStatus valueOf(int statusCode) {
		return Stream.of(values()).filter(s -> s.statusCode == statusCode).findAny()
				.orElseThrow(() -> new IllegalStateException(
						"statusCode(" + statusCode + ") does not exist."));
	}
}
