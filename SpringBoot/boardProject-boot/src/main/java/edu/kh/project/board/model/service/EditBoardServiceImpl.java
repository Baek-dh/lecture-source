package edu.kh.project.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImage;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.utility.Util;
import lombok.RequiredArgsConstructor;


@Transactional(rollbackFor = Exception.class)
@Service
@PropertySource("classpath:/config.properties")
@RequiredArgsConstructor
public class EditBoardServiceImpl implements EditBoardService{

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
	
	
	// 게시글 삽입
	@Override
	public int boardInsert(Board board, List<MultipartFile> images) throws IllegalStateException, IOException {
		
		// 1. BOARD 테이블 INSERT 하기 (제목 ,내용, 작성자, 게시판코드)
		//  -> boardNo (시퀀스로 생성한 번호) 반환 받기
		int result = mapper.boardInsert(board);
		if(result == 0) return 0; // 삽입 실패 시 0 리턴
		
		// 게시글 번호 꺼내기
		int boardNo = board.getBoardNo();
		
		
		// 2. 게시글 삽입 성공 시
		//  업로드된 이미지가 있다면 BOARD_IMG 테이블에 삽입하는 Mapper 호출
		
		// 실제 업로드된 파일의 정보를 기록할 List
		List<BoardImage> uploadList = new ArrayList<BoardImage>();
		
		
		// images에 담겨있는 파일 중 실제 업로드된 파일만 분류
		for(int i=0 ; i<images.size(); i++) {
			
			// i번째 요소에 업로드한 파일이 있다면
			if(images.get(i).getSize() > 0 ) {
				
				BoardImage img = new BoardImage();
				
				// img에 파일 정보를 담아서 uploadList에 추가
				img.setImgPath(webPath); // 웹 접근 경로
				img.setBoardNo(boardNo); // 게시글 번호
				img.setImgOrder(i); // 이미지 순서
				
				// 파일 원본명
				String fileName = images.get(i).getOriginalFilename();
				
				img.setImgOriginalName(fileName); // 원본명
				img.setImgRename( Util.fileRename(fileName) ); // 변경명    
				
				
				// 파일 객체를 BoardImage 필드가 참조하게 주소를 저장
				img.setUploadFile(images.get(i));
				
				uploadList.add(img);
			}
			
		} // 분류 for문 종료
		
		
		// 분류 작업 후 uploadList가 비어있지 않은 경우
		// == 업로드한 파일이 있다
		if( !uploadList.isEmpty() ) {
			
			// BOARD_IMG 테이블에 INSERT하는 Mapper 호출
			result = mapper.insertImageList(uploadList);
			// result == 삽입된 행의 개수 == uploadList.size()	
			
			
			// 삽입된 행의 개수와 uploadList의 개수가 같다면
			// == 전체 insert 성공
			if(result == uploadList.size()) {
				
				// 서버에 파일을 저장 (transferTo())
//				for(int i=0 ; i< uploadList.size(); i++) {
//					
//					int index = uploadList.get(i).getImgOrder();
//					
//					// 파일로 변환
//					String rename = uploadList.get(i).getImgReName();
//					
//					images.get(index).transferTo( new File(filePath + rename)  );                    
//				}
//				
				
				for(BoardImage img : uploadList) {
					img.getUploadFile().transferTo(new File(filePath + img.getImgRename()));
				}
				
			} else { // 일부 또는 전체 insert 실패
				
				// ** 웹 서비스 수행 중 1개라도 실패하면 전체 실패 **
				throw new FileUploadException(); // 예외 강제 발생
			}
		}
		
		return boardNo; 
	}
	
}
