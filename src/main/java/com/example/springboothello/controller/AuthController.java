package com.example.springboothello.controller; // 和 UserController 一样，专门对外提供 HTTP 接口

import com.example.springboothello.dto.LoginRequest; // 请求体：用户名、密码
import com.example.springboothello.dto.LoginResponse; // 响应体：token
import com.example.springboothello.entity.Account; // 数据库里的登录账号
import com.example.springboothello.exception.UnauthorizedException; // 用户名或密码错误时抛 401
import com.example.springboothello.repository.AccountRepository; // 按用户名查账号
import com.example.springboothello.security.JwtService; // 签发 JWT
import io.swagger.v3.oas.annotations.Operation; // Swagger 上显示接口说明
import io.swagger.v3.oas.annotations.tags.Tag; // Swagger 分组名
import jakarta.validation.Valid; // 先走 LoginRequest 上的 @NotBlank
import org.springframework.security.crypto.password.PasswordEncoder; // 核对明文和密文
import org.springframework.web.bind.annotation.PostMapping; // 只提供 POST 登录
import org.springframework.web.bind.annotation.RequestBody; // 从 JSON 里取出用户名密码
import org.springframework.web.bind.annotation.RequestMapping; // 路径前缀 /auth
import org.springframework.web.bind.annotation.RestController; // 返回 JSON

/**
 * 登录入口。校验账号密码，成功就返回 JWT。UserService 不参与这件事。
 */
@RestController // 这是一个 REST 接口
@RequestMapping("/auth") // 所有方法都在 /auth 下面
@Tag(name = "登录") // Swagger 里显示为「登录」
public class AuthController {

	private final AccountRepository accountRepository; // 查 accounts 表
	private final PasswordEncoder passwordEncoder; // 和预置账号时同一个编码器
	private final JwtService jwtService; // 签发 token

	public AuthController(AccountRepository accountRepository, PasswordEncoder passwordEncoder, JwtService jwtService) { // Spring 按类型注入这三个 Bean
		this.accountRepository = accountRepository; // 保存仓库
		this.passwordEncoder = passwordEncoder; // 保存编码器
		this.jwtService = jwtService; // 保存 JWT 服务
	}

	@PostMapping("/login") // 完整路径 POST /auth/login
	@Operation(summary = "登录并返回 JWT") // Swagger 标题
	public LoginResponse login(@Valid @RequestBody LoginRequest request) { // JSON 转 LoginRequest，空字段先 400
		Account account = accountRepository.findByUsername(request.getUsername()) // 按用户名找账号
				.orElseThrow(() -> new UnauthorizedException("用户名或密码错误")); // 找不到也说密码错误，避免暴露用户是否存在
		if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) { // 明文和数据库密文比对
			throw new UnauthorizedException("用户名或密码错误"); // 对不上同样 401
		}
		String token = jwtService.createToken(account.getId(), account.getUsername(), account.getRole()); // sub 用账号 id，用户名和角色当附加信息
		return new LoginResponse(token, "Bearer"); // 前端之后带 Authorization: Bearer <token>
	}
}
