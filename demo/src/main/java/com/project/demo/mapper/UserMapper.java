package com.project.demo.mapper;

import com.project.demo.dto.user.*;
import com.project.demo.model.User;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
	componentModel = "spring", 
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	uses = {User.class, UserRegisterRequestDTO.class}
)
public interface UserMapper {
	User toUser(UserRegisterRequestDTO dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserFromUserRegisterRequestDTO(UserRegisterRequestDTO dto, @MappingTarget User user);
}
