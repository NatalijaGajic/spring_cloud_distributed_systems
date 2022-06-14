package com.distributed.systems.userservice.services;

import com.distributed.systems.api.core.user.User;
import com.distributed.systems.api.core.user.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(int userId) {
        return null;
    }
}
