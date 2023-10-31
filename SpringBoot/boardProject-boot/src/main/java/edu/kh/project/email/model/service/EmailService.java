package edu.kh.project.email.model.service;

import java.util.Map;

public interface EmailService {

	int sendEmail(String htmlName, String email);

	int checkAuthKey(Map<String, Object> paramMap);

}
