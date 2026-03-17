package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.User;
import com.uniovi.sdi.bookspace.repositories.UsersRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

@NullMarked
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        User user = usersRepository.findByDni(dni);
        if (user == null) {
            throw new UsernameNotFoundException(dni);
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        String role = user.getUserRole() == null ? "ROLE_USER" : user.getUserRole();
        grantedAuthorities.add(new SimpleGrantedAuthority(role));
        return new org.springframework.security.core.userdetails.User(
                user.getDni(),
                user.getPassword(),
                grantedAuthorities
        );
    }
}