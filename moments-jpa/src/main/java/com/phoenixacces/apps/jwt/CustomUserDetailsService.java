package com.phoenixacces.apps.jwt;

import com.phoenixacces.apps.persistence.entities.authentication.User;
import com.phoenixacces.apps.persistence.repositories.authentication.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameAndActive(username, true);
        user.orElseThrow(() -> {
            return new UsernameNotFoundException("Not found user with username : " + username);
        });
        return user.map(CustomUserDetails::new).get();
    }

}
