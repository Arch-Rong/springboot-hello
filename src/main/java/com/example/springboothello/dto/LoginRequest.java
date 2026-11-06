package com.example.springboothello.dto; // DTO：只在接口进出时用，不存数据库

import jakarta.validation.constraints.NotBlank; // 空字符串也算没填

/**
 * 登录请求体，对应 POST /auth/login 的 JSON：{ "username": "admin", "password": "123456" }
 */
public class LoginRequest {

	@NotBlank(message = "用户名不能为空") // 没填用户名时走全局校验，返回 400
	private String username; // 登录名

	@NotBlank(message = "密码不能为空") // 没填密码时同样 400
	private String password; // 明文密码，只在这一次请求里出现，不会写回数据库

	public String getUsername() { // Jackson 读 JSON 字段 username 时会调用对应 setter；这里提供读取
		return username; // 返回用户名
	}

	public void setUsername(String username) { // JSON 反序列化时填入用户名
		this.username = username; // 保存用户名
	}

	public String getPassword() { // 登录时取出明文，拿去和数据库密文比对
		return password; // 返回明文密码
	}

	public void setPassword(String password) { // JSON 反序列化时填入密码
		this.password = password; // 保存明文密码
	}
}
