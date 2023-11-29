package com.kh.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class MemberController {

	@PostMapping("loginTest")
	public String loginTest(String id, String pw, Model model) {
		
		String message = "실패";
		
		if(id.equals("user01") && pw.equals("pass01"))	message = "성공";
		
		model.addAttribute("message", message);
		
		return "loginResult";  
	}
	
}
