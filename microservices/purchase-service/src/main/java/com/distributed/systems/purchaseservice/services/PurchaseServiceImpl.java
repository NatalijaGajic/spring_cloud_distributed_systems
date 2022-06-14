package com.distributed.systems.purchaseservice.services;

import com.distributed.systems.api.core.purchase.Purchase;
import com.distributed.systems.api.core.purchase.PurchaseService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseServiceImpl implements PurchaseService {
    @Override
    public Purchase getPurchase(int purchaseId) {
        return null;
    }
}
