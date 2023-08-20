package com.glimps.glimpsserver.common.constant;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.RoleTypeConverter;

class RoleTypeTest {



	@Test
	@DisplayName("RoleTypeConverter 정상 작동 확인")
	void roleType_toString() {
	    //given
		RoleType user = RoleType.USER;

		//when
		RoleType roleType = RoleTypeConverter.convert(user.toString());

		//then
		assertThat(user).isEqualTo(roleType);
	}

}
