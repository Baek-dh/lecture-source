package com.kh.test.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kh.test.board.model.dto.Board;
import com.kh.test.board.model.service.BoardService;


@Controller
public class BoardController {

	@Autowired
	private BoardService service;
	
	@GetMapping("selectBoard")
	public String selectBoard(int inputNo, Model model) {
		
		Board board = service.selectBoard(inputNo);
		
		if(board == null) return "searchFail";
		
		model.addAttribute("board", board);
		return "searchSuccess";
	}
}
