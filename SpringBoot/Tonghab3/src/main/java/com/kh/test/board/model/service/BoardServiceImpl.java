package com.kh.test.board.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.test.board.model.dto.Board;
import com.kh.test.board.model.mapper.BoardMapper;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper mapper;
	
	@Override
	public Board selectBoard(int inputNo) {
		return mapper.selectBoard(inputNo);
	}
	
}
