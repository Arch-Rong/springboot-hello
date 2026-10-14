package com.example.springboothello.service;

import com.example.springboothello.entity.User;
import com.example.springboothello.exception.ResourceNotFoundException;
import com.example.springboothello.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Page<User> listUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	public User getUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + id));
	}

	public User createUser(User user) {
		user.setId(null);
		return userRepository.save(user);
	}

	public User updateUser(Long id, User user) {
		User existing = getUser(id);
		existing.setName(user.getName());
		return userRepository.save(existing);
	}

	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("用户不存在: " + id);
		}
		userRepository.deleteById(id);
	}
}
