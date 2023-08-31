package com.kob.backend.controller.user;

import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserMapper usermapper;

    @GetMapping("/user/all/")
    public List<User> getAll(){
        return usermapper.selectList(null);
    }

    @GetMapping("/user/{userId}/")
    public User getuser(@PathVariable int userId){
        return usermapper.selectById(userId);
    }

    @GetMapping("/user/{userId}/{name}/{password}")
    public String adduser(@PathVariable int userId,
                          @PathVariable String name,
                          @PathVariable String password){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(userId, name, encodedPassword);
        usermapper.insert(user);
        return "Add Successful";
    }

    @GetMapping("/user/delete/{userId}")
    public String deleteuser(@PathVariable int userId){
        usermapper.deleteById(userId);
        return "Delete Successful";
    }


}
