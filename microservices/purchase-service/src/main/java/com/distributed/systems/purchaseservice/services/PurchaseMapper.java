package com.distributed.systems.purchaseservice.services;

import com.distributed.systems.api.core.purchase.Purchase;
import com.distributed.systems.api.core.purchase.PurchaseService;
import com.distributed.systems.purchaseservice.persistence.PurchaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Purchase entityToApi(PurchaseEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    PurchaseEntity apiToEntity(Purchase api);
}
