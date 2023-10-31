package edu.kh.project.board.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;

public interface EditBoardService {

	/** 게시글 삭제
	 * @param map
	 * @return result
	 */
	int deleteBoard(Map<String, Object> map);

	
	
	
	
}
