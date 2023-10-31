package edu.kh.project.myPage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.utility.Util;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;

@Transactional
@Service
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Value("${my.member.webpath}")
	private String webPath;
	
	@Value("${my.member.location}")
	private String filePath;

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
	
	
	// 프로필 이미지 수정 서비스
	@Override
	public int updateProfileImg(MultipartFile profileImage, Member loginMember) throws IllegalStateException, IOException {
		
		// 프로필 이미지 변경 실패 대비
		String temp = loginMember.getProfileImg(); // 이전 이미지 저장
		
		String rename = null; // 변경 이름 저장 변수
		
		if(profileImage.getSize() > 0) { // 업로드된 이미지가 있을 경우      
			
			// 1) 파일 이름 변경
			rename = Util.fileRename(profileImage.getOriginalFilename());
			
			// 2) 바뀐 이름 loginMember에 세팅
			loginMember.setProfileImg(webPath + rename);
						//  /images/member/20231101123910_00001.jpg
			
		} else { // 없는 경우(x버튼)
			loginMember.setProfileImg(null);
			// 세션 이미지를 null로 변경해서 삭제
		}
		
		
		// 프로필 이미지 수정 mapper 메서드 호출
		int result = mapper.updateProfileImg(loginMember);
		
		
		if(result > 0) { // 성공
			
			// 새 이미지가 업로드된 경우
			if(rename != null) {
				profileImage.transferTo(new File(filePath + rename));
			}
			
			
		} else { // 실패
			// 이전 이미지로 프로필 다시 세팅
			loginMember.setProfileImg(temp);
		}
		
		return result;
	}

	
}
