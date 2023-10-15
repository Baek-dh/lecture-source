package edu.kh.project.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // 요청/응답 처리 + bean 등록
@RequestMapping("/member") // 공통 주소(/member 로 시작하는 주소)
@SessionAttributes({"loginMember"}) // Model 중 "loginMember"를 Session으로 이동  
public class MemberController {

	// 등록된 bean 중 MemberServiceImpl을
	// 해당 필드에 의존성 주입
	@Autowired
	private MemberService service;
	
	
	
	
	/** 로그인 요청 처리
	 * <p>
	 * 	로그인 요청 처리(세션) + 아이디 저장(쿠키)
	 * </p>
	 * @param inputMember : 이메일, 비밀번호 저장 커맨드 객체
	 * @param model : 데이터 전달용 객체
	 * @param ra : 리다이렉트 시 데이터를 request scope로 전달하는 객체
	 * @return
	 */
	@PostMapping("login")
	public String login(Member inputMember
			, Model model
			, RedirectAttributes ra) {
		
		// 로그인 서비스 호출
		Member loginMember = service.login(inputMember);
		// 로그인 성공 : Member 객체 반환
		// 로그인 실패 : null
		
		
		/* Spring에서 Session을 다루는 방법
		 * 
		 * [1] Model 객체 + @SessionAttributes 
		 */
		
		// 데이터 전달 객체에 속성 추가
		// - 기본 scope == request scope
		model.addAttribute("loginMember", loginMember);
		
		
		
		// -----------------------------------------------------------
		// 로그인 실패 시 특정 메세지를 *잠깐* session에 세팅하여 
		// 메인페이지에서 alert()로 출력하기

		// *잠깐* session에 세팅하는 이유 : 
		// 1) redirect 시 request scope 이용 불가
		// 2) session에 계속 있으면 계속 메세지가 출력되기 때문에
		
		/* RedriectAttributes */
		// - 값 세팅 시 : request scope
		// - redirect 중 : session scope 이동
		// - redriect 완료 후 : request scope로 돌아옴
		
		// [작성법]
		// 1) RedriectAttributes를 메서드 매개변수에 추가
		// 2) RedriectAttributes.addFlashAttribute("Key", value)
		
		if(loginMember == null) { // 로그인 실패 시
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다");
		}
		
		
		// -----------------------------------------------------------
		
		log.debug(loginMember.toString());
		
		// redirect 시 기존 요청(request scope)은 삭제됨
		
		// Spring에서 redirect 하는 방법
		// return "redirect:요청주소";
		
		return "redirect:/"; // 메인 페이지 재요청
	}
	
	
	
	
	/** 로그아웃 
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		// SessionStatus : @SessionAttributes를 이용해
		//	Session scope에 등록된 값을 
		//	정리 할 수 있는(없앨 수 있는) 객체
		
		// 세션 만료(세션 제거)
		status.setComplete();
		
		return "redirect:/"; // 메인 페이지 재요청
	}
	
	
	
	
	/** 회원 가입 화면 전환
	 * @return
	 */
	@GetMapping("signup")
	public String signup() {
		
		// /WEB-INF/views/member/signup.jsp로 forward
		return "member/signup";
	}
	
	
	
	/** 회원 가입 진행
	 * @return
	 */
	@PostMapping("signup")
	public String signup(Member inputMember, 
		@RequestParam("memberAddress") String[] memberAddress,
		RedirectAttributes ra) {
		
		// RedirectAttributes : 리다이렉트 시 값을 1회성으로 전달하는 객체
		
		// memberAddress : 주소 3개가 저장된 배열
		
		// 회원 가입 서비스 호출 후 결과(INSERT 행의 개수) 반환 받기
		int result = service.signup(inputMember, memberAddress);
		
		// service 호출 결과
		// 1) 1 == INSERT 성공
		// 2) 0 == INSERT 실패
		// 3) 예외 발생 
		
		
		// 회원 가입 성공 시
		if(result > 0) {
			// 메인 페이지로 리다이렉트 후
			// "회원 가입 성공" alert() 출력
			ra.addFlashAttribute("message", "회원 가입 성공");
			return "redirect:/";
		}
		
		
		// 회원 가입 실패 시
		// 회원 가입 페이지로 리다이렉트 후
		// "가입 실패" alert() 출력
		ra.addFlashAttribute("message", "가입 실패...");
		return "redirect:/member/signup";
		
		// 참조 : 리다이렉트는 GET 방식 요청이다!
		
	}
}


