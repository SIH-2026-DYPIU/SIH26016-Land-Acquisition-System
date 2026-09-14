package com.sih.landacquisitionsystem.dto;

import lombok.*;
import com.sih.landacquisitionsystem.model.User.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {

    private String name;
    private String email;
    private String password;
    private Role role;
}