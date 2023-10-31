package com.kh.test.board.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.kh.test.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	Board selectBoard(int inputNo);

}
