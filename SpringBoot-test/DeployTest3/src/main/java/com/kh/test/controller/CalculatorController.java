package com.kh.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculatorController {
	
	
	@PostMapping("calc")
	public String calc(@RequestParam("input1") int input1, @RequestParam("input2")int input2, Model model) {
		
		String message = null;
		if(input2 == 0)  message = "0으로 나눌 수 없습니다";
		else {
			message = String.format("%d / %d = %d", input1, input2, input1/input2);
		}
		
		model.addAttribute("message", message);
		
		return "calcResult";
	}
}
