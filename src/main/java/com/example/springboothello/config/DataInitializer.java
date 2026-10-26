package com.example.springboothello.config; // 启动时预置数据的配置类

import com.example.springboothello.entity.Account; // 登录账号
import com.example.springboothello.repository.AccountRepository; // 登录账号仓库
import org.springframework.boot.CommandLineRunner; // 应用启动完成后自动跑一段代码
import org.springframework.context.annotation.Bean; // 把方法返回值交给 Spring 管理
import org.springframework.context.annotation.Configuration; // 声明这是配置类
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 把明文密码变成不可逆密文
import org.springframework.security.crypto.password.PasswordEncoder; // 密码编码器接口，后面登录也会用同一个 Bean

@Configuration // 启动时加载这个类
public class DataInitializer {

	@Bean // Spring 容器里放一个 PasswordEncoder，登录校验和这里加密共用
	PasswordEncoder passwordEncoder() { // 方法名就是 Bean 的默认名字
		return new BCryptPasswordEncoder(); // 使用 BCrypt：同样的明文每次密文不同，但都能验证通过
	}

	@Bean // 启动后执行：没有登录账号时插入 admin
	CommandLineRunner initAccounts(AccountRepository accountRepository, PasswordEncoder passwordEncoder) { // 同时注入仓库和密码编码器
		return args -> { // 启动完成后执行这段
			if (accountRepository.count() == 0) { // 还没有账号才创建，避免覆盖你后来自己加的账号
				String encodedPassword = passwordEncoder.encode("123456"); // 明文 123456 变成 BCrypt 密文
				Account admin = new Account("admin", encodedPassword, "ADMIN"); // 用户名 admin，角色 ADMIN
				accountRepository.save(admin); // 写入 accounts 表
			}
		};
	}
}
