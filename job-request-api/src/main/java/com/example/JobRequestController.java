package com.example;

import static org.springframework.http.ResponseEntity.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JobRequestController {
	private final JobRequestService jobRequestService;

	public JobRequestController(JobRequestService jobRequestService) {
		this.jobRequestService = jobRequestService;
	}

	@GetMapping(path = "v1/job_requests/{jobRequestId}")
	public ResponseEntity<?> getJobRequest(@PathVariable String jobRequestId) {
		return jobRequestService.findByJobRequestId(jobRequestId)
				.map(ResponseEntity::<Object> ok).orElse(status(HttpStatus.NOT_FOUND)
						.body(error("Not Found", jobRequestId + " is not found.")));
	}

	@PostMapping(path = "v1/job_requests")
	public ResponseEntity<Void> registerJob(@RequestBody JobRequest jobRequest) {
		String jobRequestId = jobRequestService.registerJob(jobRequest);
		URI uri = fromMethodCall(
				on(JobRequestController.class).getJobRequest(jobRequestId)).build()
						.encode().toUri();
		return created(uri).build();
	}

	@PutMapping(path = "v1/job_requests/{jobRequestId}")
	public ResponseEntity<Void> requeueJob(@PathVariable String jobRequestId,
			@RequestParam Optional<Boolean> force) {
		jobRequestService.requeueJob(jobRequestId, force.orElse(false));
		return noContent().build();
	}

	private static Map<String, Object> error(String error, String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("message", message);
		body.put("error", error);
		return body;
	}
}
