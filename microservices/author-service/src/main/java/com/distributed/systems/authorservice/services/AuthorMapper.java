package com.distributed.systems.authorservice.services;

import com.distributed.systems.api.core.author.Author;
import com.distributed.systems.authorservice.persistence.AuthorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Author entityToApi(AuthorEntity api);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    AuthorEntity apiToEntity(Author api);

    List<Author> entityListToApiList(List<AuthorEntity> entity);
    List<AuthorEntity> apiListToEntityList(List<Author> api);
}
