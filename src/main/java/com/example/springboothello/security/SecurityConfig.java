package com.example.springboothello.security; // 安全配置：哪些路径要登录、JWT 过滤器怎么挂上

import com.fasterxml.jackson.databind.ObjectMapper; // 未登录时写出 JSON，而不是 HTML 登录页
import jakarta.servlet.http.HttpServletResponse; // 设置 401 状态码
import org.springframework.boot.web.servlet.FilterRegistrationBean; // 禁止把 JwtAuthFilter 再注册成普通 Servlet 过滤器
import org.springframework.context.annotation.Bean; // 把安全链和过滤器交给 Spring
import org.springframework.context.annotation.Configuration; // 这是配置类
import org.springframework.http.MediaType; // JSON 的 Content-Type
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // 配置 HTTP 安全规则
import org.springframework.security.config.http.SessionCreationPolicy; // 不用 Session，完全靠 JWT
import org.springframework.security.web.SecurityFilterChain; // 最终生效的过滤器链
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // JWT 过滤器要插在它前面

import java.time.Instant; // 401 JSON 里的时间
import java.util.LinkedHashMap; // 保持 JSON 字段顺序
import java.util.Map; // 401 响应体

/**
 * 关掉默认的表单登录和 Session，改成：登录接口公开，其它接口必须带 JWT。
 */
@Configuration // 启动时加载
public class SecurityConfig {

	@Bean // 创建 JWT 过滤器，供安全链使用
	JwtAuthFilter jwtAuthFilter(JwtService jwtService) { // Spring 会把 JwtService 传进来
		return new JwtAuthFilter(jwtService); // 不用 @Component，避免过滤器被注册两次
	}

	@Bean // 告诉 Boot：这个 Filter 不要自动挂到 Servlet 容器，只走 Spring Security 链
	FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter jwtAuthFilter) { // 注入上面那个 Bean
		FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(jwtAuthFilter); // 包一层注册信息
		registration.setEnabled(false); // 关闭自动注册
		return registration; // 返回给 Spring
	}

	@Bean // 核心：哪些请求放行、哪些必须登录
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter, ObjectMapper objectMapper)
			throws Exception { // HttpSecurity 的构建方法声明了 Exception
		http.csrf(csrf -> csrf.disable()); // 接口用 JWT，不走浏览器表单，关闭 CSRF
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 不创建 Session
		http.httpBasic(basic -> basic.disable()); // 关掉默认的弹窗账号密码
		http.formLogin(form -> form.disable()); // 关掉默认的 /login 页面
		http.authorizeHttpRequests(auth -> auth // 开始写路径规则
				.requestMatchers("/auth/login").permitAll() // 登录接口必须公开，否则没法拿 token
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // 文档先公开，下一步再加 Authorize
				.anyRequest().authenticated()); // 其余一律要登录，包括 /users
		http.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint((request, response, exception) -> { // 没登录或 token 无效
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401
			response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 返回 JSON
			response.setCharacterEncoding("UTF-8"); // 中文不乱码
			Map<String, Object> body = new LinkedHashMap<>(); // 和全局异常处理同一套字段
			body.put("timestamp", Instant.now().toString()); // 发生时间
			body.put("status", 401); // 状态码
			body.put("message", "未登录或令牌无效"); // 给前端看的说明
			objectMapper.writeValue(response.getWriter(), body); // 写成 JSON
		}));
		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // 先解析 JWT，再判断是否已登录
		return http.build(); // 生成最终的过滤器链
	}
}
