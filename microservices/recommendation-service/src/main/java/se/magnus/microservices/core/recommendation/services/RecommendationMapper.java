package se.magnus.microservices.core.recommendation.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.microservices.core.recommendation.persistence.RecommendationEntity;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "rating", source = "rate")
    })
    RecommendationEntity dtoToEntity(Recommendation dto);

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true),
            @Mapping(target = "rate", source = "rating")
    })
    Recommendation entityToDto(RecommendationEntity entity);
}
