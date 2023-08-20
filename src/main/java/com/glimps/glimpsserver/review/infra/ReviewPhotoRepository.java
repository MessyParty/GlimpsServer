package com.glimps.glimpsserver.review.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glimps.glimpsserver.review.domain.ReviewPhoto;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {
}
