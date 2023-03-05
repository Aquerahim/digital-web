package com.phoenixacces.apps.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SenderMessage implements Serializable {
    private String content;
    private String email;
    private String subject;
    private String ccId;
    private String fromId;
    private String fromName;
    private String type;
}
