package com.example.springboothello.exception; // 和 ResourceNotFoundException 放一起

/**
 * 登录失败时抛出，例如用户名不存在或密码不对。由全局异常处理转成 HTTP 401。
 */
public class UnauthorizedException extends RuntimeException { // 非检查异常，方法签名不用写 throws

	public UnauthorizedException(String message) { // 把给前端看的中文提示传进来
		super(message); // 交给父类保存，getMessage() 时能取到
	}
}
