package com.glimps.glimpsserver.common.dev;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.glimps.glimpsserver.common.oauth.dto.OAuthUserVo;
import com.glimps.glimpsserver.perfume.domain.Brand;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.domain.PerfumeNote;
import com.glimps.glimpsserver.perfume.dto.CreateNoteRequest;
import com.glimps.glimpsserver.perfume.infra.BrandRepository;
import com.glimps.glimpsserver.perfume.infra.NoteRepository;
import com.glimps.glimpsserver.perfume.infra.PerfumeNoteRepository;
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
	private final NoteRepository noteRepository;
	private final PerfumeNoteRepository perfumeNoteRepository;


	@PostConstruct
	private void initDB() {
		User user1 = getUser("이준표", "wnsvy607@naver.com", RoleType.USER);
		User user2 = getUser("강시후", "gyeong0308@gmail.com", RoleType.USER);

		userRepository.saveAll(List.of(user1, user2));

		Brand brand = Brand.createBrand("Channel", "샤넬");
		Brand ck = Brand.createBrand("CK","캘빈클라인");
		brandRepository.saveAll(List.of(brand, ck));

		Perfume no5 = Perfume.createPerfume(brand, "NO.5");
		perfumeRepository.save(no5);

		Perfume One = Perfume.createPerfume(ck, "One");
		perfumeRepository.save(One);

		StringBuilder stringBuilder = new StringBuilder();

		Note suede = Note.createNote(new CreateNoteRequest("SUEDE", "스웨이드"));
		Note white = Note.createNote(new CreateNoteRequest("WHITE TEA", "화이트 티"));
		Note fig = Note.createNote(new CreateNoteRequest("FIG", "무화과"));

		noteRepository.saveAll(List.of(suede, white, fig));

		PerfumeNote no5Suede = PerfumeNote.mapNoteToPerfume(no5, suede);
		PerfumeNote no5White = PerfumeNote.mapNoteToPerfume(no5, white);

		PerfumeNote oneWhite = PerfumeNote.mapNoteToPerfume(One, white);
		PerfumeNote oneFig = PerfumeNote.mapNoteToPerfume(One, fig);

		perfumeNoteRepository.saveAll(List.of(no5White, no5Suede, oneWhite, oneFig));

		ReviewCreateRequest channelRequest =
			ReviewCreateRequest.builder()
				.perfumeUuid(no5.getUuid())
				.title("샤넬 향수 정말 좋아요")
				.body("샤넬 향수 본문")
				.overallRatings(5)
				.longevityRatings(3)
				.sillageRatings(2)
				.build();

		ReviewCreateRequest ckRequest =
			ReviewCreateRequest.builder()
				.perfumeUuid(One.getUuid())
				.title("ck 향수 정말 좋아요")
				.body("ck 향수 본문")
				.overallRatings(5)
				.longevityRatings(3)
				.sillageRatings(2)
				.build();

		Review review1 = Review.createReview(channelRequest, user1, no5);
		Review review2 = Review.createReview(ckRequest, user2, One);
		reviewRepository.saveAll(List.of(review1, review2));
	}

	private User getUser(String name, String email, RoleType role) {
		OAuthUserVo userVo = OAuthUserVo.builder()
			.name(name)
			.email(email)
			.userType(UserType.KAKAO)
			.build();
		return User.createUser(userVo, role);
	}

}
