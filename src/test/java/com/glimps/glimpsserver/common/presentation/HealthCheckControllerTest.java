package com.glimps.glimpsserver.common.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.glimps.glimpsserver.common.config.SecurityConfig;
import com.glimps.glimpsserver.config.WithMockCustomUser;

@WebMvcTest(controllers = HealthCheckController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class})
	})
	// @ImportAutoConfiguration(exclude = {OAuth2ClientAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
class HealthCheckControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@WithMockCustomUser
	@Test
	void HealthCheckTest() throws Exception {

		mockMvc.perform(get("/health"))
			.andExpect(MockMvcResultMatchers.status().isOk());

	}

}
