package com.kh.test.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kh.test.user.model.dto.User;
import com.kh.test.user.model.service.UserService;

@Controller
public class UesrController {

	@Autowired
	private UserService service;
	
	@GetMapping("selectUser")
	public String selectUser(String inputId, Model model) {
		
		User user = service.selectUser(inputId);
		
		if(user == null) return "searchFail";
		
		model.addAttribute("user", user);
		return "searchSuccess";
	}
}
