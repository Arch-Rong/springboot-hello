package com.example.springboothello.entity; // 登录账号所在的包，和业务用户 User 分开

import jakarta.persistence.Column; // 用来声明列名、是否为空、是否唯一
import jakarta.persistence.Entity; // 告诉 JPA：这个类对应一张数据库表
import jakarta.persistence.GeneratedValue; // 主键由数据库自动生成
import jakarta.persistence.GenerationType; // 选择自增策略 IDENTITY
import jakarta.persistence.Id; // 标记主键字段
import jakarta.persistence.Table; // 指定表名，避免和类名混用

/**
 * 登录账号。不要和 User 搞混：User 是业务数据（张三、李四），Account 才是能登录的账号。
 */
@Entity // 这个类会映射成数据库表
@Table(name = "accounts") // 表名叫 accounts，避免和 users 撞名
public class Account {

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 数据库自增，插入时不用自己填 id
	private Long id; // 账号主键

	@Column(nullable = false, unique = true, length = 50) // 登录名必填、不能重复、最长 50
	private String username; // 登录用的用户名，例如 admin

	@Column(nullable = false, length = 100) // 密码必填；BCrypt 密文大约 60 位，留 100 够用
	private String password; // 存加密后的密码，永远不要存明文

	@Column(nullable = false, length = 20) // 角色必填，例如 ADMIN
	private String role; // 权限角色，下一步鉴权会用到

	public Account() { // JPA 要求必须有无参构造，查询时靠它创建对象
	}

	public Account(String username, String password, String role) { // 创建账号时一次填齐三个字段
		this.username = username; // 保存用户名
		this.password = password; // 保存已经加密过的密码
		this.role = role; // 保存角色
	}

	public Long getId() { // 读取主键
		return id; // 返回数据库生成的 id
	}

	public void setId(Long id) { // 写入主键，一般只有 JPA 会调用
		this.id = id; // 把传入的 id 赋给字段
	}

	public String getUsername() { // 读取登录名
		return username; // 返回用户名
	}

	public void setUsername(String username) { // 写入登录名
		this.username = username; // 把传入的用户名赋给字段
	}

	public String getPassword() { // 读取加密密码，登录校验时会用到
		return password; // 返回密文
	}

	public void setPassword(String password) { // 写入加密密码
		this.password = password; // 这里应传入 BCrypt 之后的字符串
	}

	public String getRole() { // 读取角色
		return role; // 返回角色名
	}

	public void setRole(String role) { // 写入角色
		this.role = role; // 把传入的角色赋给字段
	}
}
