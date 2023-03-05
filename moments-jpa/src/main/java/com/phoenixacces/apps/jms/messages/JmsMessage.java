package com.phoenixacces.apps.jms.messages;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class JmsMessage<T> implements Serializable {
    private String label;
    private String object;
    private Class<T> type;
}
