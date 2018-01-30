### 添加注解

```java
@Case("1")
@Override
public User getUserById(Long userId) {
    return users.stream()
            .filter(u -> Objects.equals(userId, u.getId()))
            .findFirst()
            .orElse(null);
}
```

### 运行效果

```java
=====================================
Type: com.puke.user.UserServiceImpl
Method: getUserById
Param: 1
Result: User{id=1, name='用户1', age=21}
=====================================
```

