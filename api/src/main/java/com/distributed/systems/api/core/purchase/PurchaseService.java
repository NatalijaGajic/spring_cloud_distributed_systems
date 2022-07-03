package com.distributed.systems.api.core.purchase;

import com.distributed.systems.api.core.course.Course;
import org.springframework.web.bind.annotation.*;

public interface PurchaseService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/purchase \
     *   -H "Content-Type: application/json" --data \
     *   '{}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/purchase",
            consumes = "application/json",
            produces = "application/json")
    Purchase createPurchase(@RequestBody Purchase body);
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


    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/purchase/1
     *
     * @param purchaseId
     */
    @DeleteMapping(value = "/purchase/{purchaseId}")
    void deletePurchase(@PathVariable int purchaseId);
}
