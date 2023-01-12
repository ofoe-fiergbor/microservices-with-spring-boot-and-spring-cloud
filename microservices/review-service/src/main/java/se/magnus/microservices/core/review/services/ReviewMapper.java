package se.magnus.microservices.core.review.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import se.magnus.api.core.review.Review;
import se.magnus.microservices.core.review.persistence.ReviewEntity;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
    })
    ReviewEntity dtoToEntity(Review dto);

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Review entityToDto(ReviewEntity entity);
}