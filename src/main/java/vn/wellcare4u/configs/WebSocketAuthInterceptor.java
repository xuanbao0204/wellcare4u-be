package vn.wellcare4u.configs;

import java.util.Map;

import org.springframework.http.server.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import vn.wellcare4u.utils.JwtUtil;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {

	private final JwtUtil jwtUtil;

	public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {

		if (request instanceof ServletServerHttpRequest servletRequest) {
			HttpServletRequest req = servletRequest.getServletRequest();

			if (req.getCookies() != null) {
				for (var cookie : req.getCookies()) {
					if ("accessToken".equals(cookie.getName())) {

						String token = cookie.getValue();

						if (jwtUtil.validate(token)) {
							String email = jwtUtil.extractEmail(token);

							System.out.println("WS AUTH EMAIL: " + email);

							attributes.put("email", email);
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		if (exception != null) {
			System.out.println("Handshake error: " + exception.getMessage());
		}
	}
}