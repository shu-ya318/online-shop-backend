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

	User toUser(UserRegisterRequestDTO dto);

	UserLoginResponseDTO toUserLoginResponseDTO(String accessToken);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserFromUserRegisterRequestDTO(UserRegisterRequestDTO dto, @MappingTarget User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserFromUserUpdateRequestDTO(UserUpdateRequestDTO dto, @MappingTarget User user);

	@Mapping(target = "phoneNumber", expression = "java(user.getPhoneNumber() == null ? \"\" : user.getPhoneNumber())")
	@Mapping(target = "address", expression = "java(user.getAddress() == null ? \"\" : user.getAddress())")
	@Mapping(target = "birth", expression = "java(user.getBirth() == null ? \"\" : user.getBirth().toString())")
	UserResponseDTO toResponseDto(User user);
}
