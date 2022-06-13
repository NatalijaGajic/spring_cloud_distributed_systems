package com.distributed.systems.api.core.purchase;

import com.distributed.systems.api.core.course.Course;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface PurchaseService {
    /**
     *
     * @param purchaseId
     * @return
     */
    @GetMapping(
            value = "/purchase/{purchaseId}",
            produces = "application/json"
    )
    public Purchase getPurchase(@PathVariable int purchaseId);
}
