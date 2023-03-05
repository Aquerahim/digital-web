package com.phoenixacces.apps.jms.messages;

import lombok.*;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SmsMessage implements Serializable {
    //private String fromId;
    private String fromName;
    private String toId;
    private String content;
    private Long typeMessage;
    private String username;
    private String password;
    private String senderId;
    private String refCourier;
    private Long compagnieId;
}
