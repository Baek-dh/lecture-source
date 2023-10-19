package com.kh.test.customer.model.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper {

	int deleteCustomer(int customerNo);
}
