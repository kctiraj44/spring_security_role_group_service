package com.javatechie.securityrole.controller;


import com.javatechie.securityrole.common.UserConstanrs;
import com.javatechie.securityrole.entity.User;
import com.javatechie.securityrole.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {




    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/join")
    public String JoinGroup(@RequestBody User user){
        user.setRole(UserConstanrs.DEFAULT_ROLE);
        String password =user.getPassword();
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
        return "*-----Welcome to the Group ----*";
    }

    //IF logged in  user is Admin  - > Admin or moderator
    //IF logged in  user is moderator  - >  moderator

    @GetMapping("/access/{userId}/{userRole}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String giveAccessToUser(@PathVariable int userId,@PathVariable String userRole, Principal principal){
        User user =  userRepository.findById(userId).get();
        List<String> activeRoles = getRolesByLoggedInUser(principal);
        String newRole = "";
        if (activeRoles.contains(userRole)){
           newRole   = user.getRole()+","+userRole;
           user.setRole(newRole);
        }
        userRepository.save(user);
        return "*-------Hi"+user.getUsername()+"----New Role assigned to you by--"+principal.getName();
    }


    private List<String > getRolesByLoggedInUser(Principal principal){
        String roles = getLoggedInUser(principal).getRole();
        List<String> assignRole = Arrays.stream(roles.split(",")).collect(Collectors.toList());

        if(assignRole.contains("ROLE_ADMIN")){
            return Arrays.stream(UserConstanrs.ADMIN_ACCESS).collect(Collectors.toList());
        }

        if(assignRole.contains("ROLE_MODERATOR")){
            return Arrays.stream(UserConstanrs.MODERATOR_ACCESS).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private User getLoggedInUser(Principal principal){
        return userRepository.findByUsername(principal.getName()).get();
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> loadUsers(){
        return  userRepository.findAll();
    }

    @GetMapping("/test")
    @Secured("ROLE_USER")
    @PreAuthorize("hasAuthority('ROLE_USER') ")
    public String testUsersAccess(){
        return "*----This is just for testing purpose only---*";
    }




}
