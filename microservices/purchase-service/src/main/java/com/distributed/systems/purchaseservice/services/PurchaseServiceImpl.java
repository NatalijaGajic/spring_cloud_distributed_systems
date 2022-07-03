package com.distributed.systems.purchaseservice.services;

import com.distributed.systems.api.core.purchase.Purchase;
import com.distributed.systems.api.core.purchase.PurchaseService;
import com.distributed.systems.purchaseservice.persistence.PurchaseEntity;
import com.distributed.systems.purchaseservice.persistence.PurchaseRepository;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final PurchaseRepository repository;
    private final PurchaseMapper mapper;

    @Autowired
    public PurchaseServiceImpl(ServiceUtil serviceUtil, PurchaseRepository repository, PurchaseMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Purchase createPurchase(Purchase body) {
        try {
            PurchaseEntity entity = mapper.apiToEntity(body);
            PurchaseEntity newEntity = repository.save(entity);

            LOG.debug("createPurchase: entity created for purchaseId: {}", body.getPurchaseId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Purchase Id: " + body.getPurchaseId());
        }
    }

    @Override
    public Purchase getPurchase(int purchaseId) {
        if(purchaseId < 1) throw new InvalidInputException("Invalid purchaseId: "+purchaseId);
        PurchaseEntity entity = repository.findByPurchaseId(purchaseId)
                .orElseThrow(() -> new NotFoundException("No purchase found for purchaseId: " + purchaseId));

        Purchase response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("/purchase return the found purchase with purchaseId={}", purchaseId);

        return response;

    }

    @Override
    public void deletePurchase(int purchaseId) {
        LOG.debug("deletePurchase: tries to delete an entity with purchaseId: {}", purchaseId);
        repository.findByPurchaseId(purchaseId).ifPresent(e -> repository.delete(e));
    }
}
