package edu.kh.project.myPage.contoller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.service.MyPageService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("myPage")
@SessionAttributes({ "loginMember" })
public class MyPageController {

	@Autowired
	private MyPageService service;

	// 마이 페이지 화면 전환 (forward)

	@GetMapping("info") // /myPage/info
	public String info() {
		// templates/myPage/myPage-info.html
		return "myPage/myPage-info";
	}

	@GetMapping("profile")
	public String profile() {
		return "myPage/myPage-profile";
	}

	@GetMapping("changePw")
	public String changePw() {
		return "myPage/myPage-changePw";
	}

	@GetMapping("secession")
	public String secession() {
		return "myPage/myPage-secession";
	}

	// -----------------------------------------------------

	/**
	 * 회원 정보 수정
	 * 
	 * @param updateMember  : 수정된 회원 정보가 담긴 커맨드 객체
	 * @param memberAddress : 입력된 주소만 저장한 String 배열
	 * @param ra            : 리다이렉트 시 request scope로 데이터 전달
	 * @param loginMember   : 로그인한 회원 정보가 담긴 객체(Session) 
	 * 	-> 로그인한 회원 번호를 얻어와 SQL 조건으로 사용
	 * @return
	 */
	@PostMapping("info") // /myPage/info (POST)
	public String info(Member updateMember, String[] memberAddress, RedirectAttributes ra,
			@SessionAttribute("loginMember") Member loginMember) {

		// @SessionAttribute("loginMember") Member loginMember
		// -> @SessionAttributes 를 이용해서
		// session scope에 추가된 값 중
		// key가 "loginMember"인 값을 얻어와
		// Member loginMember 매개변수에 대입(주입)

		// ------------------------------------- 

		// 1. loginMember에서 로그인한 회원의 번호를 얻어와
		// updateMember에 추가
		int memberNo = loginMember.getMemberNo();
		updateMember.setMemberNo(memberNo);

		// 2. 회원 정보 수정 서비스 호출 후 결과 반환 받기
		int result = service.info(updateMember, memberAddress);

		// 3. 서비스 결과에 따라 응답 처리

		String message = null;
		if (result > 0) { // 성공 시
			message = "회원 정보가 수정 되었습니다.";

			
			// Session에 로그인된 회원 정보도 수정(동기화)
			/* DB 정보를 수정했을 뿐 session이 수정되지 않음!!! */
			// session 가지고 화면 출력 중인데 DB만 바꾸면 되겠어요?
			// -> session에 저장된 loginMember 수정
			// (얕은 복사 개념 적용)

			loginMember.setMemberNickname(updateMember.getMemberNickname());
			loginMember.setMemberTel(updateMember.getMemberTel());
			loginMember.setMemberAddress(updateMember.getMemberAddress());

		} else { // 실패 시
			message = "회원 정보 수정 실패......";
		}

		// 4. message를 ra를 이용해 값을 세팅한 후 리다이렉트
		ra.addFlashAttribute("message", message);

		// 현재 요청 주소 : http:localhost/myPage/info (POST)
		// 목표 요청 주소 : http:localhost/myPage/info (GET)
		// redirect는 GET방식 요청

		return "redirect:info"; // 상대 경로
	}

	/**
	 * 비밀번호 변경
	 * 
	 * @param currentPw   : 현재 비밀번호(파라미터)
	 * @param newPw       : 새 비밀번호(파라미터)
	 * @param loginMember : 로그인한 회원 정보
	 * @param ra          : 리다이렉트 시 데이터 전달
	 * @return
	 */
	@PostMapping("changePw")
	public String changePw(String currentPw, String newPw, @SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra) {

		// 로그인한 회원 번호
		int memberNo = loginMember.getMemberNo();

		// 비밀번호 변경 서비스 호출 후 결과 반환 받기
		int result = service.changePw(currentPw, newPw, memberNo);

		String path = null;
		String message = null;

		// 변경 성공 시
		if (result > 0) {
			message = "비밀번호가 변경 되었습니다";
			path = "redirect:info"; // 내 정보 페이지 재요청
		}

		else { // 변경 실패 시
			message = "현재 비밀번호가 일치하지 않습니다";
			path = "redirect:changePw"; // 비밀번호 변경 페이지 재요청
		}

		ra.addFlashAttribute("message", message);
		return path;
	}

	/**
	 * 회원 탈퇴
	 * 
	 * @param memberPw    : 현재 비밀번호
	 * @param loginMember : 로그인한 회원 정보
	 * @param ra          : 리다이렉트 시 데이터 전달
	 * @param status      : 세션 상태 관리(만료 시 사용)
	 * @return
	 */
	@PostMapping("secession")
	public String secession(String memberPw, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra,
			SessionStatus status) {

		// 1. 로그인한 회원 번호 얻어오기
		int memberNo = loginMember.getMemberNo();

		// 2. 회원 탈퇴 서비스 호출 후 결과 반환 받기
		int result = service.secession(memberPw, memberNo);
		// 3. 서비스 결과에 따라 응답 처리

		String path = null;
		String message = null;

		if (result > 0) { // 성공
			message = "탈퇴 되었습니다";
			path = "redirect:/"; // 메인페이지 재요청
			status.setComplete(); // 세션 만료

		} else { // 실패
			message = "비밀번호가 일치하지 않습니다";
			path = "redirect:secession"; // 탈퇴 페이지 요청
		}

		ra.addFlashAttribute("message", message);
		return path;
	}
	
	
	
	/* MultipartFile : input type="file"로 제출된 파일을 저장한 객체
	 * 
	 * [제공하는 메서드]
	 * - transferTo() : 파일을 지정된 경로에 저장(메모리 -> HDD/SSD)
	 * - getOriginalFileName() : 파일 원본명
	 * - getSize() : 파일 크기
	 * */
	
	// 프로필 이미지 수정
	@PostMapping("/profile")
	public String updateProfile(
		@RequestParam("profileImg") MultipartFile profileImg // 업로드 파일
		, @SessionAttribute("loginMember") Member loginMember // 로그인 회원
		, RedirectAttributes ra // 리다이렉트 시 메세지 전달
		, HttpSession session // 세션 객체
		)   throws IllegalStateException, IOException{
		
		
		// 프로필 이미지 수정 서비스 호출
		int result = service.updateProfileImg(profileImg, loginMember);
		
		String message = null;
		if(result > 0)	message = "프로필 이미지가 변경되었습니다";
		else			message = "프로필 변경 실패";
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:profile";
	}
	

}
