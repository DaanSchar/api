package com.voidhub.api.auth;

import com.voidhub.api.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("real")
public class RealApplicationUserDaoService implements ApplicationUserDao {

    private final UserRepository userRepository;

    @Autowired
    public RealApplicationUserDaoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return userRepository
                .findById(username)
                .map(existingUser ->
                        new ApplicationUser(
                                existingUser.getUsername(),
                                existingUser.getPassword(),
                                List.of(new SimpleGrantedAuthority(existingUser.getRole().toString())),
                                true,
                                true,
                                true,
                                true
                        )
                );
    }

}
