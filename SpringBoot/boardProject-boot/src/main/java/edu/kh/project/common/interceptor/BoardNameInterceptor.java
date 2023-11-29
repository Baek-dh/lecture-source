package edu.kh.project.common.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.kh.project.board.model.service.BoardService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// Interceptor : 요청/응답을 가로채는 객체
// Client <-> Filter <-> Dispatcher Servlet <-> Interceptor <-> Controller
@Slf4j
public class BoardNameInterceptor implements HandlerInterceptor{
	
	
	/*
	 * preHandle (전처리) :  Dispatcher Servlet -> Controller 사이
	 * postHandle (후처리) : Controller -> Dispatcher Servlet 사이
	 * afterCompletion (뷰 완성 후): View Resolver -> Dispatcher Servlet 사이
	 * */
	
	// 전처리
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	
	
	// 후처리
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		// application scope 내장 객체 얻어오기
		ServletContext application = request.getServletContext();
		
		List<Map<String, Object>> boardTypeList = (List<Map<String, Object>>)application.getAttribute("boardTypeList");
		
		String uri = request.getRequestURI();
		
		// uri.split("/") -> ["", "board", "1"]
		int boardCode = Integer.parseInt(uri.split("/")[2]);
		
		// [{boardCode=1, boardName=공지사항}, {boardCode=2, boardName=자유 게시판}, {boardCode=3, boardName=질문 게시판}]
		for(Map<String, Object> map : boardTypeList) {
			int temp =  Integer.parseInt( String.valueOf(map.get("boardCode")) );
			
			if(temp == boardCode) {
				request.setAttribute("boardName",  map.get("boardName"));
			}
		}
		
		
		
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
	
	
	
}
