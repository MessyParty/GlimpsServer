package com.glimps.glimpsserver.common.oauth.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.glimps.glimpsserver.user.domain.UserType;

class OAuthUserVoTest {

	private static final String EMAIL = "user12345@naver.com";
	private static final String NAME = "장광남";
	private static final UserType KAKAO = UserType.KAKAO;

	private static final OAuth2User oAuth2User = new DefaultOAuth2User(null, new HashMap<>() {{
		put("name", NAME);
		put("email", EMAIL);
		put("userType", KAKAO.toString().toLowerCase());
	}}, "email");

	@Test
	@DisplayName("OAuthUserVO 객체 생성")
	void createOAuthUserVO() {
		//given

		//when
		OAuthUserVo oauthUserVo = OAuthUserVo.from(oAuth2User);

		//then
		assertThat(oauthUserVo.getUserType()).isEqualTo(KAKAO);
		assertThat(oauthUserVo.getEmail()).isEqualTo(EMAIL);
		assertThat(oauthUserVo.getName()).isEqualTo(NAME);
	}

}
