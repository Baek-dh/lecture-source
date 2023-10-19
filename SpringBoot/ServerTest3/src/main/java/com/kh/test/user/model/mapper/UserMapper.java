package com.kh.test.user.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.test.user.model.dto.User;

@Mapper
public interface UserMapper {

	List<User> selectUser(String inputId);

}
