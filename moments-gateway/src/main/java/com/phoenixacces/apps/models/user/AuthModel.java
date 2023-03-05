package com.phoenixacces.apps.models.user;

import com.phoenixacces.apps.persistence.entities.authentication.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Setter
@Getter
public class AuthModel implements Serializable {
    private User user;
    private long codeOTP;
}
