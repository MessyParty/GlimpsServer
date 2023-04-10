package com.glimps.glimpsserver.user.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glimps.glimpsserver.common.config.SecurityConfig;
import com.glimps.glimpsserver.session.dto.UserInfoDto;
import com.glimps.glimpsserver.config.WithMockCustomUser;
import com.glimps.glimpsserver.user.application.UserInfoService;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.UserType;
import com.glimps.glimpsserver.user.dto.UserPatchRequest;

@WebMvcTest(controllers = UserController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class})
	})
class UserControllerTest {

	private static final String EMAIL = "wnsvy607@naver.com";
	private static final String NICKNAME = "이준표";
	private static final String ROLE = RoleType.USER.toString();
	private static final String USER_TYPE = UserType.KAKAO.name();
	private static final int REVIEW_CNT = 10;
	private static final LocalDateTime CREATED_AT = LocalDateTime.now().minusMonths(1);
	private static final UserInfoDto USER_INFO_DTO =
		UserInfoDto.builder()
			.email(EMAIL)
			.nickname(NICKNAME)
			.role(ROLE)
			.userType(USER_TYPE)
			.reviewCnt(REVIEW_CNT)
			.createdAt(CREATED_AT)
			.build();

	@MockBean
	private UserInfoService userInfoService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@WithMockCustomUser(userName = EMAIL)
	@Test
	@DisplayName("인증된 유저는 성공적으로 정보를 가져온다.")
	void given_Authenticated_When_User_Then_Success() throws Exception {
		//given
		given(userInfoService.getUserInfo(EMAIL)).willReturn(USER_INFO_DTO);

		//when
		mockMvc.perform(get("/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(EMAIL))
			.andExpect(jsonPath("$.nickname").value(NICKNAME))
			.andExpect(jsonPath("$.reviewCnt").value(REVIEW_CNT))
			.andExpect(jsonPath("$.role").value(ROLE))
			.andExpect(jsonPath("$.userType").value(USER_TYPE))
			.andExpect(jsonPath("$.createdAt").exists())
			.andDo(print());

		//then
		verify(userInfoService).getUserInfo(EMAIL);

	}

	@WithMockCustomUser
	@Test
	@DisplayName("이메일로 유저 조회에 성공하면 결과를 그대로 반환한다.")
	void given_No_When_FindByEmail_Then_Success() throws Exception {
		//given
		given(userInfoService.getUserInfo(EMAIL)).willReturn(USER_INFO_DTO);

		//when
		mockMvc.perform(get("/users/" + EMAIL))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(EMAIL))
			.andExpect(jsonPath("$.nickname").value(NICKNAME))
			.andExpect(jsonPath("$.reviewCnt").value(REVIEW_CNT))
			.andExpect(jsonPath("$.role").value(ROLE))
			.andExpect(jsonPath("$.userType").value(USER_TYPE))
			.andExpect(jsonPath("$.createdAt").exists())
			.andDo(print());

		//then
		verify(userInfoService).getUserInfo(EMAIL);
	}

	@WithMockCustomUser
	@Test
	@DisplayName("SQL 인젝션(=문자)이 요청으로 오면 실패한다.")
	void given_SQLInjection_When_PatchUser_Then_Fail() throws Exception {
		//given
		String invalidNickname = "105 OR 1=1";
		given(userInfoService.updateUser(EMAIL, new UserPatchRequest(invalidNickname))).willReturn(1L);
		String body = mapper.writeValueAsString(new UserPatchRequest(invalidNickname));

		//when
		mockMvc.perform(patch("/users")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
			//then
			.andExpect(status().is4xxClientError())
			.andDo(print());
	}

}
