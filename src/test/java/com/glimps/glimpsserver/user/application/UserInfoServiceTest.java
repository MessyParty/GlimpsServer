package com.glimps.glimpsserver.user.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.glimps.glimpsserver.session.dto.UserInfoDto;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.User;
import com.glimps.glimpsserver.user.domain.UserType;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

	private static final String EMAIL = "wnsvy607@naver.com";
	private static final String NAME = "이준표";
	private static final UserType KAKAO = UserType.KAKAO;
	private static final RoleType ROLE = RoleType.USER;
	private static final int REVIEW_CNT = 8;
	private static final User USER = User.builder()
		.email(EMAIL)
		.nickname(NAME)
		.userType(KAKAO)
		.reviewCnt(REVIEW_CNT)
		.role(ROLE)
		.build();

	@Mock
	private UserService userService;

	@InjectMocks
	private UserInfoService userInfoService;

	@Test
	@DisplayName("회원정보 조회시 유효한 값 반환")
	void given_Email_When_GetUserInfo_Then_EqualToUser() {
	    //given
		given(userService.getByEmail(EMAIL)).willReturn(USER);

	    //when
		UserInfoDto userInfo = userInfoService.getUserInfo(USER.getEmail());

		//then
		assertThat(userInfo.getEmail()).isEqualTo(EMAIL);
		assertThat(userInfo.getNickname()).isEqualTo(NAME);
		assertThat(userInfo.getUserType()).isEqualTo(KAKAO.name());
		assertThat(userInfo.getRole()).isEqualTo(ROLE.name());
		assertThat(userInfo.getReviewCnt()).isEqualTo(REVIEW_CNT);
	// 	createdAt은 검증 비용이 너무 큼
	}


}
