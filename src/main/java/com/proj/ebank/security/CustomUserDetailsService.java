package com.proj.ebank.security;

import com.proj.ebank.exceptions.NotFoundException;
import com.proj.ebank.users.entity.User;
import com.proj.ebank.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser=userRepo.findByEmail(username)
                .orElseThrow(()->new  NotFoundException("Email Not Found"));
        return AuthUser.builder().user(myUser).build();
    }

}
