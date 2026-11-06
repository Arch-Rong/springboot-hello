package com.example.springboothello.security; // 和 JwtService 放在一起

import io.jsonwebtoken.Claims; // 解析后的用户名、角色
import io.jsonwebtoken.JwtException; // token 过期、篡改、格式不对时 JJWT 抛这个
import jakarta.servlet.FilterChain; // 过滤器链条，处理完要交给下一个
import jakarta.servlet.ServletException; // 过滤器可能抛出的 Servlet 异常
import jakarta.servlet.http.HttpServletRequest; // 当前 HTTP 请求，用来读 Authorization 头
import jakarta.servlet.http.HttpServletResponse; // 当前 HTTP 响应，这里不直接写，只往下传
import org.springframework.http.HttpHeaders; // 标准头名 AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Spring Security 认识的「已登录」对象
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 权限，格式 ROLE_ADMIN
import org.springframework.security.core.context.SecurityContextHolder; // 存放当前请求的登录状态
import org.springframework.web.filter.OncePerRequestFilter; // 保证每个请求只跑一次这个过滤器

import java.io.IOException; // 读写请求时可能的 IO 异常
import java.util.List; // 权限列表

/**
 * 每个请求先经过这里：有合法 Bearer token 就把「当前用户」放进 SecurityContext，后面的接口才能通过 authenticated()。
 */
public class JwtAuthFilter extends OncePerRequestFilter { // 继承后实现 doFilterInternal 即可

	private final JwtService jwtService; // 用来解析 token

	public JwtAuthFilter(JwtService jwtService) { // 由 SecurityConfig 创建并传入
		this.jwtService = jwtService; // 保存 JwtService
	}

	@Override // 覆盖父类：真正处理每个请求
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException { // 过滤器签名要求声明这两种异常
		String header = request.getHeader(HttpHeaders.AUTHORIZATION); // 读取 Authorization 头
		if (header != null && header.startsWith("Bearer ")) { // 只有带 Bearer 前缀才当成 JWT
			String token = header.substring(7); // 去掉 "Bearer " 这 7 个字符，剩下纯 token
			try { // 解析失败时不当成已登录，让后面的规则返回 401
				Claims claims = jwtService.parseToken(token); // 验签、检查过期，取出内容
				String userId = claims.getSubject(); // sub 是账号 id，后面用 authentication.getName() 就能拿到
				String role = claims.get("role", String.class); // token 里的角色，例如 ADMIN
				SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role); // Spring 约定角色以 ROLE_ 开头
	 authentication = new UsernamePasswordAuthenticationToken(
						userId, null, List.of(authority)); // principal 放 id；密码已验过，第二参数填 null
				SecurityContextHolder.getContext().setAuthentication(authentication); // 标记这个请求已经登录
			} catch (JwtException ex) { // 过期、伪造、格式错误
				SecurityContextHolder.clearContext(); // 清掉可能残留的登录状态
			}
		}
		filterChain.doFilter(request, response); // 无论有没有 token，都要把请求传给下一个过滤器 / Controller
	}
}
