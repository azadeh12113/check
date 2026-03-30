package com.example.artwork.config;

import com.example.artwork.model.Judge;
import com.example.artwork.repository.JudgeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("e2e")
public class E2eDataLoader {

    @Bean
    CommandLineRunner seedE2eData(JudgeRepository judgeRepository) {
        return args -> {
            if (judgeRepository.count() == 0) {
                Judge judge = new Judge();
                judge.setName("Main Judge");
                judgeRepository.save(judge);
            }
        };
    }
}