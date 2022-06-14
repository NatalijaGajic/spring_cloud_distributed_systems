package com.distributed.systems.ratingservice.services;

import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.core.rating.RatingService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RatingServiceImpl implements RatingService {
    @Override
    public List<Rating> getRating(int courseId) {
        return null;
    }
}
