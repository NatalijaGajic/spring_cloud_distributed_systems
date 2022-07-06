package com.distributed.systems.lectureservice;

import com.distributed.systems.lectureservice.persistence.LectureEntity;
import com.distributed.systems.lectureservice.persistence.LectureRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {
    @Autowired
    private LectureRepository repository;

    private LectureEntity savedEntity;

    @Before
    public void setupDb() {
        repository.deleteAll().block();

        LectureEntity entity = new LectureEntity(1, 2, "a", 3, "c");
        savedEntity = repository.save(entity).block();

        assertEqualsLecture(entity, savedEntity);
    }

    @Test
    public void create() {

        LectureEntity newEntity = new LectureEntity(1, 3, "a", 3, "c");
        repository.save(newEntity).block();

        LectureEntity foundEntity = repository.findById(newEntity.getId()).block();
        assertEqualsLecture(newEntity, foundEntity);

        assertEquals(2, (long)repository.count().block());
    }

    @Test
    public void update() {
        savedEntity.setLectureTitle("a2");
        repository.save(savedEntity).block();

        LectureEntity foundEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getLectureTitle());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity).block();
        assertFalse(repository.existsById(savedEntity.getId()).block());
    }

    @Test
    public void getByProductId() {
        List<LectureEntity> entityList = repository.findByCourseId(savedEntity.getCourseId()).collectList().block();

        assertThat(entityList, hasSize(1));
        assertEqualsLecture(savedEntity, entityList.get(0));
    }

    //TODO: not throwing DuplicateKeyException
    /*@Test(expected = DuplicateKeyException.class)
    public void duplicateError() {
        LectureEntity entity = new LectureEntity(1, 2, "a", 3, "c");
        repository.save(entity);
    }*/

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        LectureEntity entity1 = repository.findById(savedEntity.getId()).block();
        LectureEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setLectureTitle("a1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setLectureTitle("a2");
            repository.save(entity2).block();

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        LectureEntity updatedEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getLectureTitle());
    }

    private void assertEqualsLecture(LectureEntity expectedEntity, LectureEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getCourseId(),        actualEntity.getCourseId());
        assertEquals(expectedEntity.getLectureId(), actualEntity.getLectureId());
        assertEquals(expectedEntity.getLectureTitle(),           actualEntity.getLectureTitle());
        assertEquals(expectedEntity.getDurationInMinutes(),           actualEntity.getDurationInMinutes());
        assertEquals(expectedEntity.getServiceAddress(),          actualEntity.getServiceAddress());
    }
}
