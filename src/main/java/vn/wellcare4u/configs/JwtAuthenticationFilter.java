package vn.wellcare4u.configs;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.utils.JwtUtil;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtService;

	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		if (path.startsWith("/ws") || path.contains("sockjs")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = null;

		if (request.getCookies() != null) {
			for (var cookie : request.getCookies()) {
				if ("accessToken".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}

		if (token != null && jwtService.validate(token)) {
			try {
				String email = jwtService.extractEmail(token);

				UserDetails userDetails = userDetailsService.loadUserByUsername(email);

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(auth);

			} catch (Exception ignored) {
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}

//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//
//		String token = null;
//
//		if (request.getCookies() != null) {
//			for (var cookie : request.getCookies()) {
//				if ("accessToken".equals(cookie.getName())) {
//					token = cookie.getValue();
//					break;
//				}
//			}
//		}
//
//		if (token == null) {
//			filterChain.doFilter(request, response);
//			return;
//		}
//
//        if (!jwtService.validate(token)) {
//        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid or expired token");
//            return;
//        }
//
//        try {
//            String email = jwtService.extractEmail(token);
//
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            
//
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Token expired or invalid");
//            return;
//        }
//
//		filterChain.doFilter(request, response);
//	}
}