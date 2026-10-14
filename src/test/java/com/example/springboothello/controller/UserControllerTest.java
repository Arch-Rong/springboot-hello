package com.example.springboothello.controller;

import com.example.springboothello.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository userRepository;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		userRepository.deleteAll();
	}

	@Test
	void createAndGetUser() throws Exception {
		String locationBody = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"李四\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("李四"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		String id = locationBody.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1");

		mockMvc.perform(get("/users/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("李四"));
	}

	@Test
	void listUsers() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"张三\"}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].name").value("张三"))
				.andExpect(jsonPath("$.totalElements").value(1));
	}

	@Test
	void updateUser() throws Exception {
		String body = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"张三\"}"))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		String id = body.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1");

		mockMvc.perform(put("/users/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"王五\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("王五"));
	}

	@Test
	void deleteUser() throws Exception {
		String body = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"临时用户\"}"))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		String id = body.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1");

		mockMvc.perform(delete("/users/" + id))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/users/" + id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("用户不存在: " + id));
	}

	@Test
	void createUserWithBlankName_shouldFail() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("参数校验失败"))
				.andExpect(jsonPath("$.fields.name").value("用户名不能为空"));
	}

	@Test
	void getMissingUser_shouldReturn404() throws Exception {
		mockMvc.perform(get("/users/99999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("用户不存在: 99999"));
	}
}
