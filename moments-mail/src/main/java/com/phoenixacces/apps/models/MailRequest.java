package com.phoenixacces.apps.models;

import com.phoenixacces.apps.enums.Importance;
import com.phoenixacces.apps.enums.Priority;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MailRequest implements Serializable {
    private String toId;
    private String toName;
    private String ccId;
    private String ccName;
    private String bccId;
    private String fromId;
    private String fromName;
    private String fromIpAddress;
    private String subject;
    private String content;
    private String mailType;
    //private List<MultipartFile> attachments;
    private String attachmentType;
    private boolean readReceipt;
    private boolean deliveryReceipt;
    private Priority priority;
    private Importance importance;
}
