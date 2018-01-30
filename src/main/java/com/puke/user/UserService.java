package com.puke.user;

import com.puke.core.Case;

import java.util.List;

public interface UserService {

    boolean save(User user);

    boolean delete(Long userId);

    User getUserById(Long userId);

    List<User> getAllUser();

}
