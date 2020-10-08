package com.example.user.repository;

import com.example.user.entity.UserDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserDao,String> {
    Mono<UserDao> findByEmailId(String email);

    Mono<UserDao> findByEmailIdAndPassword(String username,String password);
}
