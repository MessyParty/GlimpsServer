package com.glimps.glimpsserver.common.oauth.model;

import java.util.HashMap;
import java.util.Map;

import com.glimps.glimpsserver.common.error.CustomException;
import com.glimps.glimpsserver.common.error.ErrorCode;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {

	private Map<String, Object> attributes;
	private String userType;
	private String attributeKey;
	private String email;
	private String name;

	public static OAuth2Attribute of(String provider, String attributeKey, Map<String, Object> attributes) {
		switch (provider) {
			case "google":
				return ofGoogle(provider, attributeKey, attributes);
			case "kakao":
				return ofKakao(provider, attributeKey, attributes);
			default:
				throw new CustomException(ErrorCode.INVALID_PROVIDER);

		}
	}

	private static OAuth2Attribute ofGoogle(String provider, String attributeKey, Map<String, Object> attributes) {
		return OAuth2Attribute.builder()
			.userType(provider)
			.name((String)attributes.get("name"))
			.email((String)attributes.get("email"))
			.attributes(attributes)
			.attributeKey(attributeKey)
			.build();
	}

	private static OAuth2Attribute ofKakao(String provider, String attributeKey, Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

		return OAuth2Attribute.builder()
			.userType(provider)
			.name((String)kakaoProfile.get("nickname"))
			.email((String)kakaoAccount.get("email"))
			.attributes(kakaoAccount)
			.attributeKey(attributeKey)
			.build();
	}

	public Map<String, Object> convertToMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("userType", userType);
		map.put("id", attributeKey);
		map.put("key", attributeKey);
		map.put("name", name);
		map.put("email", email);

		return map;
	}
}
