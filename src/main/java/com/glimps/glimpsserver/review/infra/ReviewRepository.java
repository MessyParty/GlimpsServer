package com.glimps.glimpsserver.review.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glimps.glimpsserver.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{

}
