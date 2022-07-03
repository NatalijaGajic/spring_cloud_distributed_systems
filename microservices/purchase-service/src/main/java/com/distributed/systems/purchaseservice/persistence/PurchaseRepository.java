package com.distributed.systems.purchaseservice.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends CrudRepository<PurchaseEntity, String> {
    List<PurchaseEntity> findByCourseId(int courseId);
    Optional<PurchaseEntity> findByPurchaseId(int purchaseId);
}
