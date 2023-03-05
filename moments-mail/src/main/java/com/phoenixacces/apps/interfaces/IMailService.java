package com.phoenixacces.apps.interfaces;


import com.phoenixacces.apps.models.MailRequest;

public interface IMailService {
    void sendMail(MailRequest mail) throws Exception;
}
