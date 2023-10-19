package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/* Spring Boot Controller에서 요청 주소 작성 시 
 * 제일 앞에 "/" 제외하고 작성
 * */ 


@Slf4j // 로그
@Controller // Controller 역할(요청/응답 제어) + bean 등록
@RequestMapping("member") 
@SessionAttributes({"loginMember"}) // 세션 올리기
public class MemberController {

	// MemberService 의존성 주입
	@Autowired
	private MemberService service;
	
	/** 로그인
	 * @param inputMember : 아이디, 비밀번호 파라미터
	 * @param model : 데이터 전달 객체
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달
	 * @param saveId : 체크 박스 체크 시 on , 미체크 시 null
	 * @param resp : 응답 방법 제공 객체
	 * @return 메인 페이지(/) 리다이렉트
	 */
	@PostMapping("login") // /member/login (POST)
	public String login(Member inputMember, 
			Model model, RedirectAttributes ra, String saveId,
			HttpServletResponse resp,
			@RequestHeader("referer") String referer) {
		
		// 로그인 서비스 호출
		Member loginMember = service.login(inputMember);
		
		
		String path = null;
		// 로그인 성공 시
		if(loginMember != null) {
			path = "redirect:/";
			
			// 쿠키 생성 코드 작성 예정
			// ---------------- 아이디 저장 ----------------
			
			/* Cookie란?
			 * - 클라이언트 측(브라우저)에서 관리하는 파일
			 * 
			 * - 쿠키파일에 등록된 주소 요청 시 마다
			 *   자동으로 요청에 첨부되어 서버로 전달됨.
			 * 
			 * - 서버로 전달된 쿠키에
			 *   값 추가, 수정, 삭제 등을 진행한 후 
			 *   다시 클라이언트에게 반환
			 * */
			
			/* Session
			 * - 서버가 클라이언트의 정보를 저장하고 있음 (쿠키와의 차이점)
			 * */
			
			// 쿠키 생성(해당 쿠키에 담을 데이터를 K:V 로 지정)
			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
			
			if(saveId != null) { // 체크 되었을 때
				// 한 달(30일) 동안 유지되는 쿠키 생성
				cookie.setMaxAge(60 * 60 * 24 * 30); // 초 단위로 지정
				
			}else { // 체크 안되었을 때
				// 0초 동안 유지 되는 쿠키 생성
				// -> 기존 쿠기를 삭제
				cookie.setMaxAge(0);
			}
			
			// 클라이언트가 어떤 요청을 할때 쿠키가 첨부될지 경로(주소)를 지정
			cookie.setPath("/"); // localhost/ 이하 모든 주소
								// ex) /  , /member/login,  /member/logout 등
								//     모든 요청에 쿠키 첨부
			
			// 응답 객체(HttpServletResponse)를 이용해서 
			// 만들어진 쿠키를 클라이언트에게 전달
			resp.addCookie(cookie);
		}
		
		// 로그인 실패 시
		if(loginMember == null) {
			ra.addFlashAttribute("message", 
								"아이디 또는 비밀번호가 일치하지 않습니다");
			
			path = "redirect:" + referer;
		}
		
		// 로그인 결과를 session scope에 추가
		model.addAttribute("loginMember", loginMember);
		
		// 메인 페이지 리다이렉트
		return path;
	}
	
	
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		status.setComplete(); // @SessionAttributes 세션 만료
		return "redirect:/";
	}
	
	
	/** 로그인 전용 페이지 forward
	 * @return "member/login"
	 */
	@GetMapping("login")
	public String login() {
		return "member/login";
	}
	
	
	/** 회원 가입 페이지 forward
	 * @return
	 */
	@GetMapping("signup")
	public String signup() {
		// templates/member/signup.html로 forward
		return "member/signup";
	}
	
	
	/** 회원 가입
	 * @param inputMember : 파라미터가 저장된 커맨드 객체
	 * @param memberAddress : 주소 입력 값이 저장된 배열(가공 예정)
	 * @param ra : 리다이렉트 시 request scope로 값 전달
	 * @return
	 */
	@PostMapping("signup")
	public String signup(Member inputMember, 
			String[] memberAddress, RedirectAttributes ra ) {
		
		// 회원 가입 서비스 호출
		int result = service.signup(inputMember, memberAddress);
		
		// 회원 가입 성공 시
		if(result > 0) {
			ra.addFlashAttribute("message", "회원 가입 성공");
			return "redirect:/"; // 메인 페이지
		}
		
		// 회원 가입 실패
		ra.addFlashAttribute("message","가입 실패...");
		return "redirect:signup"; // 회원 가입 페이지 (상대경로 작성)
	}
	
	
	// @PathVariable("key")
	// - 경로(주소)를 변수에 값으로 사용하는 어노테이션
	
	/** 빠른 로그인(프로젝트 완성 후 삭제!)
	 * @param memberEmail : 주소 마지막 레벨 문자열(PathVariable)
	 * @param model : 데이터 전달용 객체
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달
	 * @return 
	 */
	@GetMapping("login/{memberEmail}")
	public String quickLogin(
		@PathVariable("memberEmail") String memberEmail,
		Model model, RedirectAttributes ra) {
		
//		log.debug("memberEmail : " + memberEmail);
		Member loginMember = service.quickLogin(memberEmail);
		
		if(loginMember == null) {
			ra.addFlashAttribute("message", "빠른 로그인 실패");
		}
		
		// 기본값 -> request scope
		// @SessionAttributes({"loginMember"}) -> session scope 이동
		model.addAttribute("loginMember", loginMember);
		
		return "redirect:/";
	}
	
	
}



