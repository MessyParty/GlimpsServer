package com.glimps.glimpsserver.session.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.common.authentication.UserAuthentication;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.common.error.InvalidTokenException;
import com.glimps.glimpsserver.common.jwt.JwtDto;
import com.glimps.glimpsserver.common.jwt.JwtUtil;
import com.glimps.glimpsserver.common.jwt.TokenType;
import com.glimps.glimpsserver.common.oauth.dto.OAuthUserVo;
import com.glimps.glimpsserver.common.util.AuthorizationHeaderUtils;
import com.glimps.glimpsserver.session.dto.AccessTokenDto;
import com.glimps.glimpsserver.user.application.UserService;
import com.glimps.glimpsserver.user.domain.User;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
	private final UserService userService;
	private final JwtUtil jwtUtil;

	public UserAuthentication authenticate(String authorizationHeader) {
		AuthorizationHeaderUtils.validateAuthorization(authorizationHeader);
		String token = authorizationHeader.split(" ")[1];

		return getAuthentication(token);
	}

	private UserAuthentication getAuthentication(String token) {
		Claims claims = jwtUtil.decode(token);
		String tokenType = (String)claims.get("token_type");

		if (!TokenType.isAccessToken(tokenType)) {
			throw new InvalidTokenException(ErrorCode.NOT_ACCESS_TOKEN_TYPE, token);
		}
		String email = claims.getSubject();
		String role = (String)claims.get("role");

		return new UserAuthentication(email, List.of(new SimpleGrantedAuthority(role)));
	}

	@Transactional
	public JwtDto oauthLogin(OAuth2User oauth2User) {
		OAuthUserVo oauthUserVo = OAuthUserVo.from(oauth2User);
		Optional<User> optionalUser = userService.getOptionalUserByEmail(oauthUserVo.getEmail());

		User user = optionalUser.orElseGet(() -> {
			Long id = userService.registerUser(oauthUserVo);
			return userService.getById(id);
		});

		return issueJwt(user);
	}

	private JwtDto issueJwt(User user) {
		JwtDto jwtDto = jwtUtil.createJwtDto(user.getEmail(), user.getRole());

		userService.updateRefreshToken(user.getId(), jwtDto.getRefreshToken(),
			jwtDto.getRefreshTokenExpireTime());

		return jwtDto;
	}


	public AccessTokenDto issueAccessToken(String authorizationHeader) {

		AuthorizationHeaderUtils.validateAuthorization(authorizationHeader);
		String refreshToken = authorizationHeader.split(" ")[1];

		User user = userService.getByRefreshToken(refreshToken);

		return jwtUtil.createAccessTokenDto(user.getEmail(), user.getRole());

	}

	@Transactional
	public Long logout(String email) {
		User user = userService.getByEmail(email);
		user.expireRefreshToken(LocalDateTime.now());
		return user.getId();
	}


}
