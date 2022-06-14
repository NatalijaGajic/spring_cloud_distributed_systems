package com.distributed.systems.purchaseservice.services;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.purchase.Purchase;
import com.distributed.systems.api.core.purchase.PurchaseService;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public PurchaseServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Purchase getPurchase(int purchaseId) {
        LOG.debug("/purchase return the found purchase with purchaseId={}", purchaseId);
        if(purchaseId < 1) throw new InvalidInputException("Invalid purchaseId: "+purchaseId);
        if(purchaseId == 13) throw  new NotFoundException("No purchase with id: "+purchaseId);
        return new Purchase(purchaseId, 2, 3, null, 8, "$", serviceUtil.getServiceAddress());
    }
}
