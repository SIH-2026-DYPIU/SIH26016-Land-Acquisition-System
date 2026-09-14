package com.sih.landacquisitionsystem.dto;

import lombok.*;
import com.sih.landacquisitionsystem.model.User.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    // password is not included in DTO for security reasons
}