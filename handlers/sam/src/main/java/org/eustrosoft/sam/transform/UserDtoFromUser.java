package org.eustrosoft.sam.transform;

import org.eustrosoft.core.db.model.User;
import org.eustrosoft.handlers.sam.dto.UserDTO;

import java.util.function.Function;

public class UserDtoFromUser implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setId(user.getId());
        return dto;
    }
}
