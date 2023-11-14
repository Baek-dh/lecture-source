package edu.kh.project.common.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;


@Component
public class ChattingHandshakeInterceptor implements HandshakeInterceptor{


	// WebsocketHandler 객체가 동작하기 전 필요한 처리를 하는 클래스
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
    	
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            
            // Map<String, Object> attributes 
            // : WebsocketHandler 클래스의 WebSocketSession에서 사용하고자 하는 값을 세팅
            //   (attributes에 추가된 값은 WebSocketSession에 그대로 복사됨)
            
//            attributes.put("loginMember", session.getAttribute("loginMember"));
            attributes.put("session", session);
        }
    	
        return true;
    }

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
	}
	
	
	
}
