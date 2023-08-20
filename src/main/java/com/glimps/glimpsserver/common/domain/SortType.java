package com.glimps.glimpsserver.common.domain;

import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SortType {
	ASC(Sort.Direction.ASC), DESC(Sort.Direction.DESC);

	private final Sort.Direction direction;
}
