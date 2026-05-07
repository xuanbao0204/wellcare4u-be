package vn.wellcare4u.configs;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        String email = (String) attributes.get("email");

        if (email == null) {
            System.out.println("WS: No user found in handshake");
            return null;
        }

        System.out.println("WS: Connected user = " + email);

        return () -> email;
    }
}