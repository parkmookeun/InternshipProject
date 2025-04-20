package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.UserNotFountException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserRepository {
    private static Long sequence = 1L;
    private static final Map<Long, User> userMemory = new HashMap<>();

    public void save(User user){
        //회원 가입이 불가능 한 경우 -> username이 동일한 경우
        for (Long key : userMemory.keySet()) {
            if(Objects.equals(userMemory.get(key).getUsername(), user.getUsername())){
                throw new CustomException();
            }
        }
        //성공 로직
        userMemory.put(sequence++, user);
    }

    public User findByUsername(String username) {
        for (Long key : userMemory.keySet()) {
            User user = userMemory.get(key);
            if(Objects.equals(user.getUsername(), username)){
                return user;
            }
        }
        throw new UserNotFountException();
    }

    public User findByUserId(Long userId) {
        for (Long key : userMemory.keySet()) {
            if(Objects.equals(key, userId)){
                return userMemory.get(key);
            }
        }
        throw new UserNotFountException();
    }

    public void delete(User user) {
        for (Long key : userMemory.keySet()) {
            if(Objects.equals(userMemory.get(key).getUsername(), user.getUsername())){
                userMemory.remove(key);
                return;
            }
        }
        throw new UserNotFountException();
    }
}
