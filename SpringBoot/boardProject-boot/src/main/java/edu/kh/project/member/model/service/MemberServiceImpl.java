package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;

@Service // bean 등록 + 비즈니스 로직 처리 역할 명시
		// -> 비즈니스 로직 : 응답에 필요한 데이터를 만드는 과정
		//		-  데이터 가공, DAO 호출, 트랜잭션 제어 등
public class MemberServiceImpl implements MemberService{

	@Autowired // 등록된 bean 의존성 주입(DI)
	private MemberMapper mapper;
	
	@Autowired // 등록된 bean 의존성 주입(DI)
	private BCryptPasswordEncoder bcrypt;
	
	// bcrypt.encode(평문) -> 암호화
	
	@Override
	public Member login(Member inputMember) {
		
		Member loginMember = mapper.login(inputMember);
		
		// DB 조회 결과가 없을 경우
		if(loginMember == null) 	return null;
		
		// 입력된 비밀번호와 DB에 저장된 암호화된 비밀번호가 일치하지 않으면
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}
		
		// 로그인된 회원 정보에서 비밀번호 제거 후 리턴
		loginMember.setMemberPw(null);
		return loginMember;
	}
	
	
	@Transactional
	@Override
	public int signup(Member inputMember, String[] memberAddress) {
		
		// memberAddress 가공
		// 주소를 입력하지 않은 경우
		if( inputMember.getMemberAddress().equals(",,") ) {
			inputMember.setMemberAddress(null);
			
		} else { // 입력한 경우
			
			// memberAddress 배열 요소의 값을 하나의 문자열 변환
			// (단, 요소 사이 구분자는 "^^^" )
			String addr = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(addr);
		}
		
		// -> 주소 입력 X == null
		// -> 주소 입력 O == "A^^^B^^^C"
		
		// --------------------------------------------------------
		// 비밀번호 암호화 진행
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);
		
		// --------------------------------------------------------
		
		// DAO 호출
		return mapper.signup(inputMember);
	}
	
	
	
	
}