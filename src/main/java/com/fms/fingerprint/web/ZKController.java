package com.fms.fingerprint.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fms.fingerprint.model.ZKContext;

@RestController
public class ZKController {
	
	@PostMapping("/register")
	public ResponseEntity<String> register() {
		ZKContext.REGISTER_MODE = true;
		ZKContext.IDENTIFY_MODE = false;
		ZKContext.enroll_idx = 0;
		return ResponseEntity.ok("Register Mode, Please Tap Finger!");
	}
	
	@PostMapping("/identify")
	public ResponseEntity<String> identify() {
		ZKContext.REGISTER_MODE = false;
		ZKContext.IDENTIFY_MODE = true;
		return ResponseEntity.ok("Identify Mode, Please Tap Finger!");
	}

}
