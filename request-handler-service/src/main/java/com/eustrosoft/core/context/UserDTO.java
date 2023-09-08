package com.eustrosoft.core.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String role;
    private String icon;

    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(String.valueOf(user.getId()));
        dto.setUsername(user.getUserName());
        dto.setIcon(null);
        return dto;
    }
}
