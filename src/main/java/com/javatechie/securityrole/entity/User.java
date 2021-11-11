package com.javatechie.securityrole.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "User_Table")
public class User {

    @Id
    @GeneratedValue
    private int id;
    private String username;
    private String password;
    private boolean active;
    private String role;
}
