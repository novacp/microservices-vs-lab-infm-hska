package de.hska.iwi.vslab.user.security;

import de.hska.iwi.vslab.user.model.User;
import de.hska.iwi.vslab.user.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Service
public class UserDetailService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return user;
    }
}
