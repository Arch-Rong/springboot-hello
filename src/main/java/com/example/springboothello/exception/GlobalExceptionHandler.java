package com.example.springboothello.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理：Controller 里抛出的异常，统一在这里收口，转成固定格式的 JSON 返回给前端。
 *
 * 没有这个类时，查不到用户会直接冒出一堆英文堆栈；有了它，前端只会收到类似：
 * { "timestamp": "...", "status": 404, "message": "用户不存在: 1" }
 *
 * @RestControllerAdvice = 监听所有 Controller，并且返回 JSON（Advice + RestController 的组合）。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 处理「资源找不到」。
	 * 例如 UserService 查不到用户时会 throw new ResourceNotFoundException("用户不存在: 1")，
	 * 这里把它转成 HTTP 404，并带上刚才那句中文提示。
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(404, ex.getMessage()));
	}

	/**
	 * 处理「请求参数没填对」。
	 * UserController 的 @Valid @RequestBody User 校验失败时，Spring 会抛出 MethodArgumentNotValidException。
	 * 这里把每个字段的错误收集到 fields 里，例如 { "name": "姓名不能为空" }，HTTP 状态码是 400。
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fields = new HashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			fields.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		Map<String, Object> body = errorBody(400, "参数校验失败");
		body.put("fields", fields);
		return ResponseEntity.badRequest().body(body);
	}

	/** 拼一份统一的错误 JSON：发生时间、状态码、给前端看的说明。 */
	private Map<String, Object> errorBody(int status, String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status);
		body.put("message", message);
		return body;
	}
}
