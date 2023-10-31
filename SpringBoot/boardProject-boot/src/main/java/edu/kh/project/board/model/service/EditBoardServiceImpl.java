package edu.kh.project.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImage;
import edu.kh.project.board.model.exception.ImageDeleteException;
import edu.kh.project.board.model.mapper.BoardDAO2;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.utility.Util;
import lombok.RequiredArgsConstructor;


@Transactional(rollbackFor = Exception.class)
@Service
@PropertySource("classpath:/config.properties")
@RequiredArgsConstructor
public class EditBoardServiceImpl implements EditBoardService{

//	private BoardDAO2 dao;
	private final EditBoardMapper mapper;

	@Value("${my.board.webpath}")
	private String webPath;
	
	@Value("${my.board.location}")
	private String filePath;
	
	
	// 게시글 삭제
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int deleteBoard(Map<String, Object> map) {
		return mapper.deleteBoard(map);
	}
	
}
