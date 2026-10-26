package com.example.springboothello.repository; // 数据访问层，专门和 accounts 表打交道

import com.example.springboothello.entity.Account; // 引入登录账号实体
import org.springframework.data.jpa.repository.JpaRepository; // Spring Data 提供的通用增删改查

import java.util.Optional; // 查不到用户时返回空，而不是 null

/**
 * 登录账号的仓库。继承 JpaRepository 后，save、findById、count 这些方法不用自己写。
 */
public interface AccountRepository extends JpaRepository<Account, Long> { // 实体是 Account，主键类型是 Long

	Optional<Account> findByUsername(String username); // 按登录名查找，登录时会用到；找不到就是 Optional.empty()
}
