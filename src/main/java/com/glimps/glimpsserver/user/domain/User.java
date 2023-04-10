package com.glimps.glimpsserver.user.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.glimps.glimpsserver.common.domain.BaseTimeEntity;
import com.glimps.glimpsserver.session.dto.SignUpRequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "email")
	private String email;

	@Column(name = "review_cnt")
	private int reviewCnt;

	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private RoleType role;

	private String refreshToken;

	private LocalDateTime tokenExpirationTime;

	@Column
	@Enumerated(EnumType.STRING)
	private UserType userType;

	public static User createUser(SignUpRequest signUpRequest, RoleType role) {
		return User.builder()
			.nickname(signUpRequest.getName())
			.email(signUpRequest.getEmail())
			.userType(signUpRequest.getUserType())
			.role(role)
			.reviewCnt(0)
			.build();
	}

	public void updateRefreshToken(String refreshToken, LocalDateTime refreshTokenExpireTime) {
		this.refreshToken = refreshToken;
		this.tokenExpirationTime = refreshTokenExpireTime;
	}

	public void addReviewCnt() {
		this.reviewCnt++;
	}

	public void deleteReviewCnt() {
		this.reviewCnt--;
	}
	public void expireRefreshToken(LocalDateTime now) {
		this.tokenExpirationTime = now;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}
}
