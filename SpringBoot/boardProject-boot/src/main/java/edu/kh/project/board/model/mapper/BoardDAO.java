package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;

//@Repository
public class BoardDAO {
	
	@Autowired
	private BoardMapper boardMapper;

	/** 게시판 종류 목록 조회
	 * @return boardTypeList
	 */
	public List<Map<String, Object>> selectBoardTypeList() {
		//return boardMapper.selectList("boardMapper.selectBoardTypeList");
		
		return boardMapper.selectBoardTypeList();
	}

	/** 특정 게시판의 삭제되지 않은 게시글 수 조회
	 * @param boardCode
	 * @return listCount
	 */
	public int getListCount(int boardCode) {
		return boardMapper.getListCount(boardCode);
	}

	/** 특정 게시판에서 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
	 * @param pagination
	 * @param boardCode
	 * @return
	 */
	public List<Board> selectBoardList(Pagination pagination, int boardCode) {
		
		// 1) offset 계산
		int offset 
		= (pagination.getCurrentPage() - 1) * pagination.getLimit();
		
		// 2) RowBounds 객체 생성
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		// 3) selectList("namespace.id", 파라미터 , RowBounds ) 호출
		return boardMapper.selectBoardList(boardCode, rowBounds);                  
	}

	
	/** 게시글 상세 조회
	 * @param map
	 * @return board
	 */
	public Board selectBoard(Map<String, Object> map) {
		return boardMapper.selectBoard(map);
	}

	/** 좋아요 여부 확인 서비스
	 * @param map
	 * @return result
	 */
	public int boardLikeCheck(Map<String, Object> map) {
		return boardMapper.boardLikeCheck(map);
	}

	/** 좋아요 테이블 삽입
	 * @param paramMap
	 * @return result
	 */
	public int insertBoardLike(Map<String, Integer> paramMap) {
		return boardMapper.insertBoardLike(paramMap);
	}
	
	/** 좋아요 테이블 삭제
	 * @param paramMap
	 * @return result
	 */
	public int deleteBoardLike(Map<String, Integer> paramMap) {
		return boardMapper.deleteBoardLike(paramMap);
	}

	/** 좋아요 개수 조회
	 * @param boardNo
	 * @return count
	 */
	public int countBoardLike(Integer boardNo) {
		return boardMapper.countBoardLike(boardNo);
	}

	
	/** 조회 수 증가
	 * @param boardNo
	 * @return result
	 */
	public int updateReadCount(int boardNo) {
		return boardMapper.updateReadCount(boardNo);
	}

	/** 게시글 수 조회(검색)
	 * @param paramMap
	 * @return listCount
	 */
	public int getListCount(Map<String, Object> paramMap) {
		return boardMapper.getListCount(paramMap);
	}

	/** 게시글 목록 조회(검색)
	 * @param pagination
	 * @param paramMap
	 * @return boardList
	 */
	public List<Board> selectBoardList(Pagination pagination, Map<String, Object> paramMap) {

		// 1) offset 계산
		int offset 
		= (pagination.getCurrentPage() - 1) * pagination.getLimit();
		
		// 2) RowBounds 객체 생성
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		return boardMapper.selectBoardList(paramMap, rowBounds);
	}
	
	/** 헤더 검색
	 * @param query
	 * @return list
	 */
	public List<Map<String, Object>> headerSearch(String query) {
		return boardMapper.headerSearch(query);
	}

	
	/** DB 이미지(파일) 목록 조회
	 * @return list
	 */
	public List<String> selectImageList() {
		return boardMapper.selectImageList();
	}

	
	
}
