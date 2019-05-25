package com.base;

import com.base.dao.UserMapper;
import com.base.entity.po.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplicationTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("zw");
        userMapper.insert(userEntity);
        System.out.println(userEntity);
    }
}

