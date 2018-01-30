package com.puke.user;

import com.puke.core.Case;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserServiceImpl implements UserService {

    private final List<User> users = new ArrayList<>();

    public UserServiceImpl() {
        // 初始化数据集
        IntStream.range(0, 100).boxed()
                .map(i -> new User(Long.valueOf(String.valueOf(i)), "用户" + i, 20 + i))
                .forEach(users::add);
    }

    @Override
    public boolean save(User user) {
        Long id = user.getId();
        if (id == null) {
            List<Long> ids = users.stream()
                    .map(User::getId)
                    .sorted()
                    .collect(Collectors.toList());
            long newId = ids.get(ids.size() - 1) + 1;
            user.setId(newId);
            return users.add(user);
        } else {
            return users.stream()
                    .filter(u -> Objects.equals(id, u.getId()))
                    .findFirst()
                    .map(u -> {
                        u.setId(user.getId());
                        u.setName(user.getName());
                        u.setAge(user.getAge());
                        return true;
                    })
                    .orElseThrow(() -> new RuntimeException("Can't find user that id is " + id));
        }
    }

    @Override
    public boolean delete(Long userId) {
        return users.stream()
                .filter(u -> Objects.equals(userId, u.getId()))
                .findFirst()
                .map(users::remove)
                .orElse(false);
    }

    @Case("1")
    @Override
    public User getUserById(Long userId) {
        return users.stream()
                .filter(u -> Objects.equals(userId, u.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getAllUser() {
        return Collections.unmodifiableList(users);
    }

}
