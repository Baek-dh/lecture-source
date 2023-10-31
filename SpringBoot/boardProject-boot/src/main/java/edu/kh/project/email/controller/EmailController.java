package edu.kh.project.email.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.email.model.service.EmailService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("email")
@RequiredArgsConstructor
public class EmailController {
	
	private final EmailService service;
	
	// @Autowired를 이용한 DI 방법은 필드, Setter, 생성자 3가지가 있는데
	// 제일 권장 되는 방법은 생성자를 이용한 방법이다.
	// -> 하지만 쓰기 귀찮음
	// --> 이를 해결하기 위해 Lombok에서 제공하는 @RequiredArgsConstructor 사용
	
	// @RequiredArgsConstructor
	// 초기화 되지 않은 final 필드 또는  @NotNull이 붙은 필드에 대한 
	// 생성자 주입 방식 코드를 만들어주는 어노테이션
	
	
//	@Autowired
//	public EmailController(EmailService service) {
//		this.service = service;
//	}

	@PostMapping("signup")
	public int signup(@RequestBody String email) {
		return service.sendEmail("signup", email);
	}
	
	@PostMapping("checkAuthKey")
	public int checkAuthKey(@RequestBody Map<String, Object> paramMap) {
		return service.checkAuthKey(paramMap);
	}
}
