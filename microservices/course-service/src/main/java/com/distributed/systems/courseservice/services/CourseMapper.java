package com.distributed.systems.courseservice.services;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.courseservice.persistence.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Course entityToApi(CourseEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    CourseEntity apiToEntity(Course api);

}
