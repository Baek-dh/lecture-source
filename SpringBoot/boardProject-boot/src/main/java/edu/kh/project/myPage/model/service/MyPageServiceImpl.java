package edu.kh.project.myPage.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;

@Transactional
@Service
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public int info(Member updateMember, String[] memberAddress) {
		// 1) memberAddress 가공 처리
		// 주소를 입력하지 않은 경우
		if( updateMember.getMemberAddress().equals(",,") ) {
			updateMember.setMemberAddress(null);
			
		} else { // 입력한 경우
			
			// memberAddress 배열 요소의 값을 하나의 문자열 변환
			// (단, 요소 사이 구분자는 "^^^" )
			String addr = String.join("^^^", memberAddress);
			updateMember.setMemberAddress(addr);
		}
		
			
		// 2) dao 호출 후 결과를 반환 받아 
		//    바로 Controller로 반환
		return mapper.info(updateMember);
	}

	@Override
	public int changePw(String currentPw, String newPw, int memberNo) {
		// 1. 로그인한 회원의 암호화된 비밀번호 조회(SELECT)
		String encPw = mapper.selectMemberPw(memberNo);
		
		// 2. 현재 비밀번호와 조회한 비밀번호가 같은지 확인
		// 같으면 -> 비밀번호 변경
		// 다르면 -> return 0
		
		// BCrypt에서 제공하는 matches() 이용 
		if(!bcrypt.matches(currentPw, encPw)) {
			// 현재 비밀번호와 조회한 비밀번호가 다른 경우
			return 0;
		}
		
		// 3. 비밀번호 변경 mapper 메서드 호출 전
		// newPw, memberNo를 하나의 객체에 저장
		
		// 왜? 마이바티스 코드는 
		//	파라미터를 하나만 전달할 수 있어서!!
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newPw", bcrypt.encode(newPw)); // 새 비밀번호 암호화
		map.put("memberNo", memberNo);
		
		// 4. DAO 메서드 호출 후 반환된 결과를
		// Contrller로 반환 + @Transactional
		return mapper.changePw(map);
	}

	@Override
	public int secession(String memberPw, int memberNo) {
		// 입력 받은 비밀번호가
		// DB에 저장된 로그인한 회원의 비밀번호와 같다면
		// 회원 탈퇴 수행 후 결과 반환
		String encPw = mapper.selectMemberPw(memberNo);
		
		if(!bcrypt.matches(memberPw, encPw)) { // 다르면
			return 0;
		}
		
		return mapper.secession(memberNo);
	}
	
	
	
}
