package com.glimps.glimpsserver.common.dev;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.glimps.glimpsserver.common.oauth.dto.OAuthUserVo;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.infra.BrandRepository;
import com.glimps.glimpsserver.perfume.infra.PerfumeRepository;
import com.glimps.glimpsserver.review.domain.Review;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.infra.ReviewRepository;
import com.glimps.glimpsserver.user.domain.RoleType;
import com.glimps.glimpsserver.user.domain.User;
import com.glimps.glimpsserver.user.domain.UserType;
import com.glimps.glimpsserver.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;

@Profile({"dev", "local"})
@RequiredArgsConstructor
@Component
public class DevelopmentEnvMaker {

	private final UserRepository userRepository;
	private final PerfumeRepository perfumeRepository;
	private final BrandRepository brandRepository;
	private final ReviewRepository reviewRepository;


	@PostConstruct
	private void initDB() {
		User user1 = getUser("이준표", "wnsvy607@naver.com", RoleType.USER);
		User user2 = getUser("강시후", "gyeong0308@gmail.com", RoleType.USER);

		userRepository.saveAll(List.of(user1, user2));

		Brand brand = Brand.createBrand("Channel");
		Brand ck = Brand.createBrand("CK");
		brandRepository.saveAll(List.of(brand, ck));

		Perfume perfume1 = Perfume.createPerfume(brand, "NO.5");

		perfumeRepository.save(perfume1);

		Perfume perfume2 = Perfume.createPerfume(ck, "One");
		perfumeRepository.save(perfume2);

		ReviewCreateRequest channelRequest =
			ReviewCreateRequest.builder()
				.perfumeUuid(perfume1.getUuid())
				.title("샤넬 향수 정말 좋아요")
				.body("샤넬 향수 본문")
				.overallRatings(5)
				.longevityRatings(3)
				.sillageRatings(2)
				.build();

		ReviewCreateRequest ckRequest =
			ReviewCreateRequest.builder()
				.perfumeUuid(perfume2.getUuid())
				.title("ck 향수 정말 좋아요")
				.body("ck 향수 본문")
				.overallRatings(5)
				.longevityRatings(3)
				.sillageRatings(2)
				.build();

		Review review1 = Review.createReview(channelRequest, user1, perfume1);
		Review review2 = Review.createReview(ckRequest, user2, perfume2);
		reviewRepository.saveAll(List.of(review1, review2));
	}

	private User getUser(String name, String email, RoleType role) {
		OAuthUserVo userVo = OAuthUserVo.builder()
			.name(name)
			.email(email)
			.userType(UserType.KAKAO)
			.build();
		User user = User.createUser(userVo, role);
		return user;
	}

}
