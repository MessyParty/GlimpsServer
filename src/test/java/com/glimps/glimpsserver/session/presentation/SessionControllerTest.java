package com.glimps.glimpsserver.session.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import com.glimps.glimpsserver.common.config.SecurityConfig;
import com.glimps.glimpsserver.config.WithMockCustomUser;

@WebMvcTest(controllers = SessionController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class})
	})
class SessionControllerTest {

	@Autowired
	private MockMvc mockMvc;


	@WithMockCustomUser
	@Test
	@DisplayName("인증된 유저는 성공적으로 정보를 가져온다.")
	void given_Authenticated_When_User_Then_Success() throws Exception {

		//when
		mockMvc.perform(get("/session/state").param("provider","kakao"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.state").exists())
			.andDo(print());
	}

}
