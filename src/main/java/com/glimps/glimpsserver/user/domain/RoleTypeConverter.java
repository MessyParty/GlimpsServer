package com.glimps.glimpsserver.user.domain;

public class RoleTypeConverter {
	private RoleTypeConverter() {
	}

	public static RoleType convert(String name) {
		String of = name.substring(5, name.length());
		return RoleType.valueOf(of);
	}

}
