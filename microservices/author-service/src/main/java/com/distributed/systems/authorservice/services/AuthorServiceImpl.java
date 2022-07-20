package com.distributed.systems.authorservice.services;

import com.distributed.systems.api.core.author.Author;
import com.distributed.systems.api.core.author.AuthorService;
import com.distributed.systems.authorservice.persistence.AuthorEntity;
import com.distributed.systems.authorservice.persistence.AuthorRepository;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AuthorServiceImpl implements AuthorService {

    private final ServiceUtil serviceUtil;

    private static final Logger LOG = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorMapper mapper;
    private final AuthorRepository repository;

    public AuthorServiceImpl(ServiceUtil serviceUtil, AuthorMapper mapper, AuthorRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Author createAuthor(Author body) {
        if (body.getCourseId() < 1) throw new InvalidInputException("Invalid courseId: " + body.getCourseId());

        AuthorEntity entity = mapper.apiToEntity(body);
        Mono<Author> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Course Id: " + body.getCourseId() + ", Author Id:" + body.getAuthorId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public Flux<Author> getAuthors(int courseId) {

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        return repository.findByCourseId(courseId)
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public void deleteAuthors(int courseId) {
        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.debug("deleteAuthors: tries to delete authors for the course with courseId: {}", courseId);
        repository.deleteAll(repository.findByCourseId(courseId)).block();
    }

}
