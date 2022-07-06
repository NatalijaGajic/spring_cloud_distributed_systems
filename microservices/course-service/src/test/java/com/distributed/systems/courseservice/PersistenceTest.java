package com.distributed.systems.courseservice;

import com.distributed.systems.courseservice.persistence.CourseEntity;
import com.distributed.systems.courseservice.persistence.CourseRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.*;
import static org.springframework.data.domain.Sort.Direction.ASC;



@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTest {
    @Autowired
    private CourseRepository repository;

    private CourseEntity savedEntity;

    @Before
    public void setupDb() {

        //StepVerifier - class to set up a sequence of processing steps that execute the repo and verify the result
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        CourseEntity entity = new CourseEntity(1, "n", 1);
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return areCourseEqual(entity, savedEntity);
                })
                .verifyComplete();
    }

    @Test
    public void create() {

        CourseEntity newEntity = new CourseEntity(2, "n", 2);

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(createdEntity -> newEntity.getCourseId() == createdEntity.getCourseId())
                .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
                .expectNextMatches(foundEntity -> areCourseEqual(newEntity, foundEntity))
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

    @Test
    public void update() {


        savedEntity.setCourseTitle("n2");
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getCourseTitle().equals("n2"))
                .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getCourseTitle().equals("n2"))
                .verifyComplete();
    }

    @Test
    public void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

   @Test
    public void getByCourseId() {
       StepVerifier.create(repository.findByCourseId(savedEntity.getCourseId()))
               .expectNextMatches(foundEntity -> areCourseEqual(savedEntity, foundEntity))
               .verifyComplete();

    }

    //TODO: not throwing DuplicateKeyException
  /*@Test(expected = DuplicateKeyException.class)
    public void duplicateError() {
        CourseEntity entity = new CourseEntity(savedEntity.getCourseId(), "n", 1);
        repository.save(entity);
    }*/

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        CourseEntity entity1 = repository.findById(savedEntity.getId()).block();
        CourseEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setCourseTitle("n1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getCourseTitle().equals("n1"))
                .verifyComplete();

    }

    private boolean areCourseEqual(CourseEntity expectedEntity, CourseEntity actualEntity){
        return (expectedEntity.getId().equals(actualEntity.getId()) &&
        expectedEntity.getVersion().equals(actualEntity.getVersion()) &&
        (expectedEntity.getCourseId() == actualEntity.getCourseId()) &&
        expectedEntity.getCourseTitle().equals(actualEntity.getCourseTitle()) &&
        expectedEntity.getPrice() == actualEntity.getPrice());
    }

}
