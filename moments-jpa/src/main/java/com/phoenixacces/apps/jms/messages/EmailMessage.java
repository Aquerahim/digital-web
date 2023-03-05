package com.phoenixacces.apps.jms.messages;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmailMessage {
    private Long id;
    private String content;
    private String email;
    private String subject;
    private String type;
    private String sts;
    private String other;
    private String username;
    private String defaulPwd;
}
