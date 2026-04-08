package com.example.artwork.config;

import com.example.artwork.model.Judge;
import com.example.artwork.repository.JudgeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedJudge(JudgeRepository judgeRepository) {
        return args -> {
            if (judgeRepository.count() == 0) {
                Judge judge = new Judge();
                judge.setName("Default Judge");
                judgeRepository.save(judge);
            }
        };
    }
}