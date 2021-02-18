package de.hska.iwi.vslab.user;

import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.hska.iwi.vslab.user.model.User;
import de.hska.iwi.vslab.user.repository.UserRepo;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public Principal userInfo(@AuthenticationPrincipal Principal user) {
        return user;
    }

    // add user
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> addUser(@RequestBody User user) {

        if(!userRepo.findById(user.getUsername()).isPresent()){

            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            return new ResponseEntity<>(userRepo.save(user), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(null, HttpStatus.CONFLICT);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable String username) {

        Optional<User> optionalUser = userRepo.findById(username);
        return optionalUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    //get user role
    @RequestMapping(value = "/{username}/is-admin", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Boolean> getUserRole(@PathVariable String username) {

        Optional<User> optionalUser = userRepo.findById(username);
        return optionalUser.map(user -> new ResponseEntity<>(user.getAdmin(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}