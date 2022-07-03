package com.distributed.systems.lectureservice.services;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.lectureservice.persistence.LectureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LectureMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Lecture entityToApi(LectureEntity api);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    LectureEntity apiToEntity(Lecture api);

    List<Lecture> entityListToApiList(List<LectureEntity> entity);
    List<LectureEntity> apiListToEntityList(List<Lecture> api);
}
