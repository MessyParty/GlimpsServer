package com.glimps.glimpsserver.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewOrderStandard {
	DATE("createdAt"), HEARTS_COUNT("heartCnt");

	private final String property;

}
