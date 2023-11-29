package edu.kh.wearher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherController {
	
	@GetMapping("ex1")
	public String ex1() {
		return "ex1";
	}
	
}
