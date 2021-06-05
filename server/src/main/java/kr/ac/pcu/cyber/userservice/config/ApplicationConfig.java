package kr.ac.pcu.cyber.userservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }

}
