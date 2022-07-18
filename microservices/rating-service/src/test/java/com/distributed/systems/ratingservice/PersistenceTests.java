package com.distributed.systems.ratingservice;

import com.distributed.systems.ratingservice.persistence.RatingEntity;
import com.distributed.systems.ratingservice.persistence.RatingRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest(properties = {"spring.cloud.config.enabled=false"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PersistenceTests {
    @Autowired
    private RatingRepository repository;

    private RatingEntity savedEntity;

    @Before
    public void setupDb() {
        repository.deleteAll();

        RatingEntity entity = new RatingEntity(1, 2, 2, 5, "c");
        savedEntity = repository.save(entity);

        assertEqualsRating(entity, savedEntity);
    }

    @Test
    public void create() {

        RatingEntity newEntity = new RatingEntity(1, 3, 2, 5, "c");
        repository.save(newEntity);

        RatingEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsRating(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }
    @Test
    public void update() {
        savedEntity.setText("cc");
        repository.save(savedEntity);

        RatingEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("cc", foundEntity.getText());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
    public void getByProductId() {
        List<RatingEntity> entityList = repository.findByCourseId(savedEntity.getCourseId());

        assertThat(entityList, hasSize(1));
        assertEqualsRating(savedEntity, entityList.get(0));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void duplicateError() {
        RatingEntity entity = new RatingEntity(1, 2, 2, 5, "c");
        repository.save(entity);
    }

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        RatingEntity entity1 = repository.findById(savedEntity.getId()).get();
        RatingEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setText("a1");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setText("a2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        RatingEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getText());
    }

    private void assertEqualsRating(RatingEntity expectedEntity, RatingEntity actualEntity) {
        assertEquals(expectedEntity.getId(),        actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),   actualEntity.getVersion());
        assertEquals(expectedEntity.getCourseId(), actualEntity.getCourseId());
        assertEquals(expectedEntity.getRatingId(),  actualEntity.getRatingId());
        assertEquals(expectedEntity.getUserId(),    actualEntity.getUserId());
        assertEquals(expectedEntity.getStarRating(),   actualEntity.getStarRating());
        assertEquals(expectedEntity.getText(),   actualEntity.getText());
    }
}
