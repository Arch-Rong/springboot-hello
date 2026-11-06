package com.example.springboothello.security; // 鉴权相关的类放在 security 包，和业务 Service 分开

import io.jsonwebtoken.Claims; // token 解析后的内容：谁、什么角色、何时过期
import io.jsonwebtoken.Jwts; // JJWT 的入口，用来签发和解析
import io.jsonwebtoken.security.Keys; // 把配置里的字符串密钥变成 HMAC 密钥
import org.springframework.beans.factory.annotation.Value; // 从 application.yaml 读取配置
import org.springframework.stereotype.Service; // 交给 Spring 管理，登录和过滤器都能注入它

import javax.crypto.SecretKey; // HMAC 签名用的密钥类型
import java.nio.charset.StandardCharsets; // 把密钥字符串按 UTF-8 转成字节
import java.time.Instant; // 当前时间和过期时间
import java.util.Date; // JJWT 的 issuedAt、expiration 仍然用 Date

/**
 * 负责签发 JWT、解析 JWT。登录成功后调用 createToken；每次请求由过滤器调用 parseToken。
 */
@Service // 让 Spring 创建这一份，整应用共用
public class JwtService {

	private final SecretKey key; // 签名和验签用的同一把密钥，丢了或改了，旧 token 全部失效
	private final long expireHours; // token 多少小时后过期

	public JwtService(
			@Value("${app.jwt.secret}") String secret, // 从 yaml 读取 app.jwt.secret
			@Value("${app.jwt.expire-hours}") long expireHours) { // 从 yaml 读取过期小时数
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // 字符串密钥 → HMAC-SHA 密钥
		this.expireHours = expireHours; // 保存过期时间，签发时计算 expiration
	}

	public String createToken(Long userId, String username, String role) { // 登录成功后签发：用稳定的账号 id 认人
		Instant now = Instant.now(); // 签发时刻
		Instant expiresAt = now.plusSeconds(expireHours * 3600); // 从现在起过 expireHours 小时
		return Jwts.builder() // 开始组装一份 JWT
				.subject(String.valueOf(userId)) // 标准字段 sub：账号主键，改用户名也不会变
				.claim("username", username) // 可选：展示或打日志用，不当身份主键
				.claim("role", role) // 自定义字段：角色，过滤器里会读出来
				.issuedAt(Date.from(now)) // 何时签发
				.expiration(Date.from(expiresAt)) // 何时过期，过期后 parseToken 会失败
				.signWith(key) // 用密钥签名，别人伪造不了
				.compact(); // 输出最终的三段式字符串：header.payload.signature
	}

	public Claims parseToken(String token) { // 校验签名和过期时间，成功则返回 id、用户名、角色
		return Jwts.parser() // 开始解析
				.verifyWith(key) // 必须能用同一把密钥验签，否则抛异常
				.build() // 得到可以解析的 parser
				.parseSignedClaims(token) // 解析并校验，过期或篡改会抛异常
				.getPayload(); // 取出 claims：subject、role、exp 等
	}
}
