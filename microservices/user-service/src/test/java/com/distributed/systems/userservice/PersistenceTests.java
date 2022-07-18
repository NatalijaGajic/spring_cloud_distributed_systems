package com.distributed.systems.userservice;


import com.distributed.systems.userservice.persistence.UserEntity;
import com.distributed.systems.userservice.persistence.UserRepository;
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
@DataMongoTest(properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests {
    @Autowired
    private UserRepository repository;

    private UserEntity savedEntity;

    @Before
    public void setupDb() {
        repository.deleteAll();

        UserEntity entity = new UserEntity(1, "fn","em");
        savedEntity = repository.save(entity);

        assertEqualsPurchase(entity, savedEntity);
    }

    @Test
    public void create() {

        UserEntity newEntity = new UserEntity(1,"fn", "em");
        repository.save(newEntity);

        UserEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsPurchase(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void update() {
        savedEntity.setFullName("new");
        repository.save(savedEntity);

        UserEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("new", foundEntity.getFullName());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    //TODO: not throwing DuplicateKeyException
    /*@Test(expected = DuplicateKeyException.class)
    public void duplicateError() {
        UserEntity entity = new UserEntity(1, "f", "e");
        repository.save(entity);
    }*/

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        UserEntity entity1 = repository.findById(savedEntity.getId()).get();
        UserEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setFullName("m");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setFullName("m");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        UserEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("m", updatedEntity.getFullName());
    }

    private void assertEqualsPurchase(UserEntity expectedEntity, UserEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getUserId(),        actualEntity.getUserId());
        assertEquals(expectedEntity.getFullName(),           actualEntity.getFullName());
        assertEquals(expectedEntity.getEmail(),           actualEntity.getEmail());
    }
}
