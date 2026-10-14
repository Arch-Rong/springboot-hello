package com.example.springboothello.controller;

import com.example.springboothello.entity.User;
import com.example.springboothello.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "用户管理")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	@Operation(summary = "分页查询用户")
	public Page<User> listUsers(@PageableDefault(size = 10) Pageable pageable) {
		return userService.listUsers(pageable);
	}

	@GetMapping("/{id}")
	@Operation(summary = "按 ID 查询用户")
	public User getUser(@PathVariable Long id) {
		return userService.getUser(id);
	}

	@PostMapping
	@Operation(summary = "创建用户")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新用户")
	public User updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
		return userService.updateUser(id, user);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除用户")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
