package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 로그 출력
@Controller
public class MainController {
	
	@RequestMapping("/")
	public String mainPage() {
		log.debug("로그는 이렇게 찍어요");
		return "common/main";
	}
}
