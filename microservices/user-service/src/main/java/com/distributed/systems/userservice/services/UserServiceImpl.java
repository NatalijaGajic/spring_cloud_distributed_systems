package com.distributed.systems.userservice.services;

import com.distributed.systems.api.core.user.User;
import com.distributed.systems.api.core.user.UserService;
import com.distributed.systems.userservice.persistence.UserEntity;
import com.distributed.systems.userservice.persistence.UserRepository;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(ServiceUtil serviceUtil, UserRepository repository, UserMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User createUser(User body) {
        try {
            UserEntity entity = mapper.apiToEntity(body);
            UserEntity newEntity = repository.save(entity);

            LOG.debug("createUser: entity created for userId: {}", body.getUserId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, User Id: " + body.getUserId());
        }
    }

    @Override
    public User getUser(int userId) {

        if(userId < 1) throw new InvalidInputException("Invalid userId: "+userId);
        UserEntity entity = repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("No user found for userId: " + userId));

        User response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getUser: found userId: {}", response.getUserId());
        return response;

    }

    @Override
    public void deleteUser(int userId) {
        LOG.debug("deleteUser: tries to delete an entity with userId: {}", userId);
        repository.findByUserId(userId).ifPresent(e -> repository.delete(e));
    }
}
