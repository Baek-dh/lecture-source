package com.kh.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MemberController {

	@GetMapping("check")
	public String check(@RequestParam("inputId") String inputId, Model model) {

		String message = "사용 가능한 아이디 입니다";
		
		if(inputId.equals("user01")) message = "이미 사용 중인 아이디 입니다";
		
		model.addAttribute("message",message);
		
		return "checkResult";
	}
}
