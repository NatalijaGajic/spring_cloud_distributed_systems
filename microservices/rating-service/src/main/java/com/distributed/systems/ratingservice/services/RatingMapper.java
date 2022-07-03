package com.distributed.systems.ratingservice.services;

import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.ratingservice.persistence.RatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Rating entityToApi(RatingEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    RatingEntity apiToEntity(Rating api);

    List<Rating> entityListToApiList(List<RatingEntity> entity);
    List<RatingEntity> apiListToEntityList(List<Rating> api);
}
