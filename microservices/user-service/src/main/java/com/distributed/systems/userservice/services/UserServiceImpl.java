package com.distributed.systems.userservice.services;

import com.distributed.systems.api.core.purchase.Purchase;
import com.distributed.systems.api.core.user.User;
import com.distributed.systems.api.core.user.UserService;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private final ServiceUtil serviceUtil;

    @Autowired
    public UserServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public User getUser(int userId) {

        LOG.debug("/user return the found user with userId={}", userId);
        if(userId < 1) throw new InvalidInputException("Invalid userId: "+userId);
        if(userId == 13) throw  new NotFoundException("No user with id: "+userId);
        return new User(userId, "Natalija Gajic", null, null, serviceUtil.getServiceAddress());
    }
}
