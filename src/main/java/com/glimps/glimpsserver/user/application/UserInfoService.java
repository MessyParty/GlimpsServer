package com.glimps.glimpsserver.user.application;

import org.springframework.stereotype.Service;

import com.glimps.glimpsserver.session.dto.UserInfoDto;
import com.glimps.glimpsserver.user.domain.User;
import com.glimps.glimpsserver.user.dto.UserPatchRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserInfoService {

	private final UserService userService;


	public UserInfoDto getUserInfo(String email) {
		User user = userService.getByEmail(email);
		return UserInfoDto.of(user);
	}

	public Long updateUser(String email, UserPatchRequest request) {
		return userService.updateUser(email, request.getNickname());
	}
}
