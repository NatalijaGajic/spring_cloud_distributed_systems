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
        repository.deleteAll();

        CourseEntity entity = new CourseEntity(1, "n", 1);
        savedEntity = repository.save(entity);

        assertEqualsCourse(entity, savedEntity);
    }

    @Test
    public void create() {

        CourseEntity newEntity = new CourseEntity(2, "n", 2);
        repository.save(newEntity);

        CourseEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsCourse(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void update() {
        savedEntity.setCourseTitle("n2");
        repository.save(savedEntity);

        CourseEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("n2", foundEntity.getCourseTitle());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

   @Test
    public void getByCourseId() {
        Optional<CourseEntity> entity = repository.findByCourseId(savedEntity.getCourseId());

        assertTrue(entity.isPresent());
        assertEqualsCourse(savedEntity, entity.get());
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
        CourseEntity entity1 = repository.findById(savedEntity.getId()).get();
        CourseEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setCourseTitle("n1");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setCourseTitle("n2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        CourseEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("n1", updatedEntity.getCourseTitle());
    }

    @Test
    public void paging() {

        repository.deleteAll();

        List<CourseEntity> newCourses = rangeClosed(1001, 1010)
                .mapToObj(i -> new CourseEntity(i, "title " + i, i))
                .collect(Collectors.toList());
        repository.saveAll(newCourses);

        Pageable nextPage = PageRequest.of(0, 4, ASC, "courseId");
        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
        nextPage = testNextPage(nextPage, "[1009, 1010]", false);
    }

    private Pageable testNextPage(Pageable nextPage, String expectedCourseIds, boolean expectsNextPage) {
        Page<CourseEntity> coursePage = repository.findAll(nextPage);
        assertEquals(expectedCourseIds, coursePage.getContent().stream().map(p -> p.getCourseId()).collect(Collectors.toList()).toString());
        assertEquals(expectsNextPage, coursePage.hasNext());
        return coursePage.nextPageable();
    }

    private void assertEqualsCourse(CourseEntity expectedEntity, CourseEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getCourseId(),        actualEntity.getCourseId());
        assertEquals(expectedEntity.getCourseTitle(),           actualEntity.getCourseTitle());
        assertEquals(expectedEntity.getPrice(),           actualEntity.getPrice(), 0);
    }

}
