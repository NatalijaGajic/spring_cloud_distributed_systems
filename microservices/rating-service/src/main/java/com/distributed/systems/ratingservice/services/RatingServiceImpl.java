package com.distributed.systems.ratingservice.services;

import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.core.rating.RatingService;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RatingServiceImpl implements RatingService {

    private static final Logger LOG = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public RatingServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Rating> getRatings(int courseId) {
        if(courseId < 0) throw new InvalidInputException("Invalid courseId: "+courseId);
        if(courseId == 123){
            LOG.debug("No lectures found for courseId: {}", courseId);
            return new ArrayList<>();
        }
        List<Rating> list = new ArrayList<>();
        list.add(new Rating(1, courseId, 1, 4, "Excellent", serviceUtil.getServiceAddress()));
        list.add(new Rating(2, courseId, 4, 2, "Terrible", serviceUtil.getServiceAddress()));
        list.add(new Rating(3, courseId, 5, 4, "Good", serviceUtil.getServiceAddress()));
        LOG.debug("/ratings response size: {}", list.size());

        return list;
    }
}
