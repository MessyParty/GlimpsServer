package com.glimps.glimpsserver.session.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.common.oauth.dto.JwtTokenDto;
import com.glimps.glimpsserver.common.oauth.dto.OAuthUserVo;
import com.glimps.glimpsserver.common.util.DateTimeUtils;
import com.glimps.glimpsserver.common.util.JwtUtil;
import com.glimps.glimpsserver.user.application.UserService;
import com.glimps.glimpsserver.user.domain.User;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
	private final UserService userService;
	private final JwtUtil jwtUtil;

	public String parseToken(String accessToken) {
		Claims claims = jwtUtil.decode(accessToken);
		return claims.get("email", String.class);
	}

	// TODO 삭제 필요
	public List<User> getRoles(String email) {
		return userService.findAllByEmail(email);
	}

	@Transactional
	public JwtTokenDto oauthLogin(OAuth2User oauth2User) {
		OAuthUserVo oauthUserVo = OAuthUserVo.from(oauth2User);
		Optional<User> optionalUser = userService.getOptionalUserByEmail(oauthUserVo.getEmail());

		User user = optionalUser.orElseGet(() -> {
			Long id = userService.registerUser(oauthUserVo);
			return userService.findById(id);
		});

		return issueJwt(user);
	}

	private JwtTokenDto issueJwt(User user) {
		JwtTokenDto jwtTokenDto = jwtUtil.createJwtTokenDto(user.getEmail(), user.getRole());
		LocalDateTime convertedExpTime = DateTimeUtils.convertToLocalDateTime(
			jwtTokenDto.getRefreshTokenExpireTime());
		user.updateRefreshToken(jwtTokenDto.getRefreshToken(), convertedExpTime);

		return jwtTokenDto;
	}
}
