package com.puke;

import com.puke.user.User;
import com.puke.user.UserService;
import com.puke.user.UserServiceImpl;

public class Main {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        User saveUser = new User(null, "newName", 100);
        System.out.println(userService.save(saveUser));
        System.out.println(saveUser);
        User user = userService.getUserById(100L);
        System.out.println(user);
    }

}
