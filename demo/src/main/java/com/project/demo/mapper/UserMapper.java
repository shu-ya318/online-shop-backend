package com.project.demo.mapper;

import com.project.demo.dto.user.*;
import com.project.demo.model.User;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
	// Request
	User toUser(UserRegisterRequestDTO dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserFromUserRegisterRequestDTO(UserRegisterRequestDTO dto, @MappingTarget User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserFromUserUpdateRequestDTO(UserUpdateRequestDTO dto, @MappingTarget User user);

	// Response
	@Mapping(source = "phoneNumber", target = "phoneNumber", defaultExpression = "java(\"\")")
	@Mapping(source = "address", target = "address", defaultExpression = "java(\"\")")
	@Mapping(source = "birth", target = "birth", defaultExpression = "java(\"1900-01-01\")")
	UserResponseDTO toUserResponseDTO(User user);
}
