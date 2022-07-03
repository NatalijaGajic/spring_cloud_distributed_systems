package com.distributed.systems.ratingservice.services;

import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.core.rating.RatingService;
import com.distributed.systems.ratingservice.persistence.RatingEntity;
import com.distributed.systems.ratingservice.persistence.RatingRepository;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RatingServiceImpl implements RatingService {

    private static final Logger LOG = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final RatingRepository repository;
    private final RatingMapper mapper;

    @Autowired
    public RatingServiceImpl(ServiceUtil serviceUtil, RatingRepository repository, RatingMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
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
    public List<Rating> getRatings(int courseId) {
        if(courseId < 0) throw new InvalidInputException("Invalid courseId: "+courseId);

        List<RatingEntity> entityList = repository.findByCourseId(courseId);
        List<Rating> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getRatings: response size: {}", list.size());
        //list.add(new Rating(1, courseId, 1, 4, "Excellent", serviceUtil.getServiceAddress()));

        return list;
    }

    @Override
    public void deleteRatings(int courseId) {
        LOG.debug("deleteRatings: tries to delete ratings for the course with courseId: {}", courseId);
        repository.deleteAll(repository.findByCourseId(courseId));
    }
}
