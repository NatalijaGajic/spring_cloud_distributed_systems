package com.distributed.systems.ratingservice.services;

import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.core.rating.RatingService;
import com.distributed.systems.ratingservice.persistence.RatingEntity;
import com.distributed.systems.ratingservice.persistence.RatingRepository;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.http.ServiceUtil;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.function.Supplier;

import static java.util.logging.Level.FINE;

@RestController
public class RatingServiceImpl implements RatingService {

    private static final Logger LOG = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final RatingRepository repository;
    private final RatingMapper mapper;

    private final Scheduler scheduler;

    @Autowired
    public RatingServiceImpl(ServiceUtil serviceUtil, RatingRepository repository, RatingMapper mapper, Scheduler scheduler) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
        this.scheduler = scheduler;
    }

    @Override
    public Rating createRating(Rating body) {

        try {
            RatingEntity entity = mapper.apiToEntity(body);
            RatingEntity newEntity = repository.save(entity);

            LOG.debug("createRating: created a rating entity: {}/{}", body.getCourseId(), body.getRatingId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Rating Id: " + body.getRatingId() + ", Rating Id:" + body.getRatingId());
        }
    }

    @Override
    public Flux<Rating> getRatings(int courseId) {

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.info("Will get ratings for course with id={}", courseId);

        return asyncFlux(() -> Flux.fromIterable(getByCourseId(courseId))).log(null, FINE);
    }

    //Run the blocking code in a thread from the thread pool
    private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
        return Flux.defer(publisherSupplier).subscribeOn(scheduler);
    }

    //Blocking code
    protected List<Rating> getByCourseId(int courseId) {

        List<RatingEntity> entityList = repository.findByCourseId(courseId);
        List<Rating> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getRatings: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteRatings(int courseId) {
        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);
        LOG.debug("deleteRatings: tries to delete ratings for the course with courseId: {}", courseId);
        repository.deleteAll(repository.findByCourseId(courseId));
    }
}
