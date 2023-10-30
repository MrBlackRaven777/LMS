package lms.webinars.configurations;

import lms.webinars.domain.entities.WebinarEntity;
import lms.webinars.dto.WebinarDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    ModelMapper getMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STANDARD);
        mapper.addMappings(new PropertyMap<WebinarDto, WebinarEntity>() {

            @Override
            protected void configure() {
                skip(destination.getFiles());
            }
        });
        return mapper;
    }

}
