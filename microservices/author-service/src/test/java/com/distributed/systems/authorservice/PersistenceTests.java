package com.distributed.systems.authorservice;

import com.distributed.systems.api.core.author.Author;
import com.distributed.systems.authorservice.persistence.AuthorEntity;
import com.distributed.systems.authorservice.persistence.AuthorRepository;
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
@DataMongoTest(properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests {
    @Autowired
    private AuthorRepository repository;

    private AuthorEntity savedEntity;

    @Before
    public void setupDb() {
        repository.deleteAll().block();

        AuthorEntity entity = new AuthorEntity(1, 2, "a", "s", 1);
        savedEntity = repository.save(entity).block();

        assertEqualsAuthor(entity, savedEntity);
    }

    @Test
    public void create() {

        AuthorEntity newEntity = new AuthorEntity(2, 2, "a", "s", 1);
        repository.save(newEntity).block();

        AuthorEntity foundEntity = repository.findById(newEntity.getId()).block();
        assertEqualsAuthor(newEntity, foundEntity);

        assertEquals(2, (long)repository.count().block());
    }

    @Test
    public void update() {
        savedEntity.setFullName("a2");
        repository.save(savedEntity).block();

        AuthorEntity foundEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getFullName());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity).block();
        assertFalse(repository.existsById(savedEntity.getId()).block());
    }

    @Test
    public void getByProductId() {
        List<AuthorEntity> entityList = repository.findByCourseId(savedEntity.getCourseId()).collectList().block();

        assertThat(entityList, hasSize(1));
        assertEqualsAuthor(savedEntity, entityList.get(0));
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
        AuthorEntity entity1 = repository.findById(savedEntity.getId()).block();
        AuthorEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setFullName("a1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setFullName("a2");
            repository.save(entity2).block();

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        AuthorEntity updatedEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getFullName());
    }

    private void assertEqualsAuthor(AuthorEntity expectedEntity, AuthorEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getCourseId(),        actualEntity.getCourseId());
        assertEquals(expectedEntity.getAuthorId(), actualEntity.getAuthorId());
        assertEquals(expectedEntity.getFullName(),           actualEntity.getFullName());
        assertEquals(expectedEntity.getNumberOfLectures(),           actualEntity.getNumberOfLectures());
        assertEquals(expectedEntity.getServiceAddress(),          actualEntity.getServiceAddress());
    }
}
