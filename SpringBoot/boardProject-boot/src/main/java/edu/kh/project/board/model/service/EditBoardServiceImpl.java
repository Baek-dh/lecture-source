package edu.kh.project.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.exception.ImageDeleteException;
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
	private String folderPath;
	
	
	// 게시글 삭제
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
		List<BoardImg> uploadList = new ArrayList<BoardImg>();
		
		
		// images에 담겨있는 파일 중 실제 업로드된 파일만 분류
		for(int i=0 ; i<images.size(); i++) {
			
			// i번째 요소에 업로드한 파일이 있다면
			if(images.get(i).getSize() > 0 ) {
				
				BoardImg img = new BoardImg();
				
				// img에 파일 정보를 담아서 uploadList에 추가
				img.setImgPath(webPath); // 웹 접근 경로
				img.setBoardNo(boardNo); // 게시글 번호
				img.setImgOrder(i); // 이미지 순서
				
				// 파일 원본명
				String fileName = images.get(i).getOriginalFilename();
				
				img.setImgOriginalName(fileName); // 원본명
				img.setImgRename( Util.fileRename(fileName) ); // 변경명    
				
				
				// 파일 객체를 BoardImg 필드가 참조하게 주소를 저장
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
				
				for(BoardImg img : uploadList) {
					img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));
				}
				
			} else { // 일부 또는 전체 insert 실패
				
				// ** 웹 서비스 수행 중 1개라도 실패하면 전체 실패 **
				throw new RuntimeException(); // 예외 강제 발생
			}
		}
		
		return boardNo; 
	}
	
	
	// 게시글 수정
	@Override
	public int boardUpdate(Board board, List<MultipartFile> images, String deleteOrder) throws IllegalStateException, IOException {
		
		// 1. 게시글 제목/내용만 수정
		int result = mapper.boardUpdate(board);
		
		
		// 2. 게시글 부분이 수정 성공 했을 때
		if(result > 0) {
			
			if(!deleteOrder.equals("")) { // 삭제할 이미지가 있다면
				
				// 3. deleteList에 작성된 이미지 모두 삭제
				Map<String, Object> deleteMap = new HashMap<>();
				deleteMap.put("boardNo", board.getBoardNo());
				deleteMap.put("deleteOrder", deleteOrder);
				
				result = mapper.imageDelete(deleteMap);
				
				if(result == 0) { // 이미지 삭제 실패 시 전체 롤백
									// -> 예외 강제로 발생
					throw new ImageDeleteException();
				}
			}
			
			
			// 4. 새로 업로드된 이미지 분류 작업
			
			// images : 실제 파일이 담긴 List
			//         -> input type="file" 개수만큼 요소가 존재
			//         -> 제출된 파일이 없어서 MultipartFile 객체가 존재
			
			List<BoardImg> uploadList = new ArrayList<>();
			
			for(int i=0 ; i<images.size(); i++) {
				
				if(images.get(i).getSize() > 0) { // 업로드된 파일이 있을 경우
					
					// BoardImg 객체를 만들어 값 세팅 후 
					// uploadList에 추가
					BoardImg img = new BoardImg();
					
					// img에 파일 정보를 담아서 uploadList에 추가
					img.setImgPath(webPath); // 웹 접근 경로
					img.setBoardNo(board.getBoardNo()); // 게시글 번호
					img.setImgOrder(i); // 이미지 순서
					
					// 파일 원본명
					String fileName = images.get(i).getOriginalFilename();
					
					img.setImgOriginalName(fileName); // 원본명
					img.setImgRename( Util.fileRename(fileName) ); // 변경명    
					
					// 파일 객체를 BoardImg 필드가 참조하게 주소를 저장
					img.setUploadFile(images.get(i));
					
					uploadList.add(img);
					
					// 오라클은 다중 UPDATE를 지원하지 않기 때문에
					// 하나씩 UPDATE 수행
					
					result = mapper.imageUpdate(img);
					
					if(result == 0) {
						// 수정 실패 == DB에 이미지가 없었다 
						// -> 이미지를 삽입
						result = mapper.imageInsert(img);
					}
				}
			}
			
			
			// 5. uploadList에 있는 이미지들만 서버에 저장(transferTo())
			if(!uploadList.isEmpty()) {
				for(BoardImg img : uploadList) {
					img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));
				}
				
			}
		}
		
		
		return result;
		
		
	}
}
