package com.glimps.glimpsserver.common.oauth.dto;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.glimps.glimpsserver.session.dto.SignUpRequest;
import com.glimps.glimpsserver.user.domain.UserType;

import lombok.Builder;
import lombok.Getter;

/**
 * OAuth2UserService 에서
 * 처리된 OAuthUser 정보를 파싱해서 갖는 VO Class
 * User.createUser() 메서드에서 Arg로 활용
 */
@Getter
public class OAuthUserVo implements SignUpRequest {

	private final String name;
	private final String email;
	private final UserType userType;

	public static OAuthUserVo from(OAuth2User oAuth2User) {
		String type = oAuth2User.getAttribute("userType");
		UserType of = UserType.valueOf(type.toUpperCase());
		return OAuthUserVo.builder()
			.email(oAuth2User.getAttribute("email"))
			.name(oAuth2User.getAttribute("name"))
			.userType(of)
			.build();
	}

	@Builder
	private OAuthUserVo(String name, String email, UserType userType) {
		this.name = name;
		this.email = email;
		this.userType = userType;
	}

}
