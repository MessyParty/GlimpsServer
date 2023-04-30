package com.glimps.glimpsserver.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.glimps.glimpsserver.perfume.infra.PerfumeCustomRepository;
import com.glimps.glimpsserver.perfume.infra.PerfumeCustomRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;

@TestConfiguration
public class EntityManagerConfig {

	@PersistenceContext
	private EntityManager em;

	@Bean
	public JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}
	@Bean
	public PerfumeCustomRepository perfumeCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
		return new PerfumeCustomRepositoryImpl(em, jpaQueryFactory);
	}
}
