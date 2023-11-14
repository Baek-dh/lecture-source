package edu.kh.project.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import edu.kh.project.chatting.model.websocket.ChattingWebsocketHandler;
import edu.kh.project.main.model.websocket.TestWebsocketHandler;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketConfigurer {
	
	
	private final TestWebsocketHandler testWebsocketHandler;
	
	private final ChattingWebsocketHandler chattingWebsocketHandler;
	
	private final HandshakeInterceptor handshakeInterceptor;
	

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		// addHandler : 웹소켓 요청을 처리할 클래스, 매핑할 요청 주소 작성
		
		// setAllowedOriginPatterns : 웹 소켓 요청을 허용할 주소 패턴 지정
		
		// withSockJS :  SockJS(websocket 보완) 지원 + 브라우저 호환성 증가
		
		
		registry.addHandler(testWebsocketHandler, "/testSock")
			.addInterceptors(handshakeInterceptor)
			.setAllowedOriginPatterns("http://localhost/", "http://127.0.0.1/", "http://192.168.10.12/")
			.withSockJS();
		
		registry.addHandler(chattingWebsocketHandler, "/chattingSock")
			.addInterceptors(handshakeInterceptor)
			.setAllowedOriginPatterns("http://localhost/", "http://127.0.0.1/", "http://192.168.10.12/")
			.withSockJS();
	}
	
}
