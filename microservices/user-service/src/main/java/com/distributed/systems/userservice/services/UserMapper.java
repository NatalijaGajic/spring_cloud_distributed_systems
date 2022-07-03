package com.distributed.systems.userservice.services;

import com.distributed.systems.api.core.user.User;
import com.distributed.systems.userservice.persistence.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    User entityToApi(UserEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    UserEntity apiToEntity(User api);
}
