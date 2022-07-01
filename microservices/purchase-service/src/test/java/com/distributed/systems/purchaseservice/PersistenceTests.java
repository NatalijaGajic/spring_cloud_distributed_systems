package com.distributed.systems.purchaseservice;

import com.distributed.systems.purchaseservice.persistence.PurchaseEntity;
import com.distributed.systems.purchaseservice.persistence.PurchaseRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {
    @Autowired
    private PurchaseRepository repository;

    private PurchaseEntity savedEntity;

    @Before
    public void setupDb() {
        repository.deleteAll();

        PurchaseEntity entity = new PurchaseEntity(1, 2, 3, 7);
        savedEntity = repository.save(entity);

        assertEqualsPurchase(entity, savedEntity);
    }

    @Test
    public void create() {

        PurchaseEntity newEntity = new PurchaseEntity(1, 3, 3, 3);
        repository.save(newEntity);

        PurchaseEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsPurchase(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void update() {
        savedEntity.setPurchasedPrice(10);
        repository.save(savedEntity);

        PurchaseEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals(10, foundEntity.getPurchasedPrice(), 0);
    }

    @Test
    public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
    public void getByProductId() {
        List<PurchaseEntity> entityList = repository.findByCourseId(savedEntity.getCourseId());

        assertThat(entityList, hasSize(1));
        assertEqualsPurchase(savedEntity, entityList.get(0));
    }

    //TODO: not throwing DuplicateKeyException
    /*@Test(expected = DuplicateKeyException.class)
    public void duplicateError() {
        PurchaseEntity entity = new PurchaseEntity(1, 2, 3, 5);
        repository.save(entity);
    }*/

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        PurchaseEntity entity1 = repository.findById(savedEntity.getId()).get();
        PurchaseEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setPurchasedPrice(20);
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setPurchasedPrice(20);
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        PurchaseEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals(20, updatedEntity.getPurchasedPrice(), 0);
    }

    private void assertEqualsPurchase(PurchaseEntity expectedEntity, PurchaseEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getCourseId(),        actualEntity.getCourseId());
        assertEquals(expectedEntity.getUserId(),           actualEntity.getUserId());
        assertEquals(expectedEntity.getPurchasedPrice(),           actualEntity.getPurchasedPrice(), 0);
    }
}
