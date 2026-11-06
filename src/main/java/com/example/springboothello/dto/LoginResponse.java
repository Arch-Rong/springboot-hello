package com.example.springboothello.dto; // 登录成功后返回给前端的数据

/**
 * 登录成功的响应：前端把 token 存下来，之后每次请求放到 Authorization 头里。
 */
public class LoginResponse {

	private String token; // JWT 字符串
	private String tokenType; // 固定为 Bearer，告诉前端请求头怎么写

	public LoginResponse(String token, String tokenType) { // 登录成功时一次构造完整响应
		this.token = token; // 保存刚签发的 JWT
		this.tokenType = tokenType; // 保存类型，一般是 Bearer
	}

	public String getToken() { // Jackson 序列化时输出 token 字段
		return token; // 返回 JWT
	}

	public String getTokenType() { // Jackson 序列化时输出 tokenType 字段
		return tokenType; // 返回 Bearer
	}
}
