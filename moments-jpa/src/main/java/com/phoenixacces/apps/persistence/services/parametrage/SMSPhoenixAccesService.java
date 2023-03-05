package com.phoenixacces.apps.persistence.services.parametrage;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.phoenixacces.apps.persistence.entities.module.ServiceCourrier;
import com.phoenixacces.apps.persistence.entities.service.SmsEnvoye;
import com.phoenixacces.apps.persistence.repositories.parametrage.SmsEnvoyeRepository;
import com.phoenixacces.apps.persistence.services.module.ServiceCourrierService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class SMSPhoenixAccesService {

    @Autowired
    private Environment env;

    private final SmsEnvoyeRepository smsEnvoyeRepository;
    private final SmsCredentialService smsCredentialService;
    private final ServiceCourrierService serviceCourrierService;

    @Autowired
    public SMSPhoenixAccesService(SmsEnvoyeRepository smsEnvoyeRepository, SmsCredentialService smsCredentialService,
                                  ServiceCourrierService serviceCourrierService) {
        this.smsEnvoyeRepository = smsEnvoyeRepository;
        this.smsCredentialService = smsCredentialService;
        this.serviceCourrierService = serviceCourrierService;
    }


    public List<SmsEnvoye> allSmsEnvoye() {
        return smsEnvoyeRepository.findAll();
    }


    public List<SmsEnvoye> findAll(String username) {
        return smsEnvoyeRepository.findByNumeroDestinataireAndActiveOrderByIdDesc(username, true).orElseGet(ArrayList::new);
    }


    public List<SmsEnvoye> findAll(ServiceCourrier serviceCourrier) {
        return smsEnvoyeRepository.findByServiceCourrierAndActiveOrderByIdDesc(serviceCourrier, true).orElseGet(ArrayList::new);
    }


    public void envoyeMessageBySouscrivant (SmsEnvoye sms) throws UnirestException {

        try {

            Instant instant = Instant.now();

            String gateway = Objects.requireNonNull(env.getProperty("api.sms.gateway"));

            log.info("[ MESSAGE SMS SERVICE ] GATE-WAY SELECTED ---------------- -------------  <{}>", gateway);

            if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                String token            = "Bearer "+sms.getSmsCredential().getToken();
                String sender           = ""+sms.getSmsCredential().getSenderLeTexto();
                String campaignName     = "ENVOI APPLICATION "+sms.getSmsCredential().getSenderLeTexto();
                String campaignType     = "SIMPLE";
                String recipientSource  = "CUSTOM";
                String destination      = "NAT";
                String Message          = ""+sms.getCorpsMessage();
                String phone            = ""+sms.getNumeroDestinataire();

                String requestObject = "{  \"step\": null,  \"sender\": \""+sender+"\""
                        + ",  \"name\": \""+campaignName+"\""
                        + ",  \"campaignType\": \""+campaignType+"\",  \"recipientSource\": \""+recipientSource+"\""
                        + ",  \"groupId\": null"
                        + ",  \"filename\": null,  \"saveAsModel\": false"
                        + ",  \"destination\": \""+destination+"\""
                        + ",  \"message\": \""+Message+"\",  \"emailText\": null"
                        + ",  \"recipients\": [{\"phone\": \""+phone+"\"}]"
                        + ",  \"sendAt\": []"
                        + ",  \"dlrUrl\": \"http://dlr.my.domain.com\""
                        + ", \"responseUrl\": \"http://res.my.domain.com\"}";

                Unirest.setTimeouts(0, 0);
                HttpResponse<JsonNode> response = Unirest.post("http://api.letexto.com/v1/campaigns")
                        .header("Authorization", token)
                        .header("Content-Type", "application/json")
                        .body(requestObject)
                        .asJson();

                //System.out.println("requestObject ===>" + requestObject);

                String id = (String) response.getBody().getObject().get("id");

                if(id != null && response.getStatus() == 200 && response.getStatusText().equalsIgnoreCase("200")){

                    HttpResponse<JsonNode> resp = Unirest.post("http://api.letexto.com/v1/campaigns/"+id+"/schedules")
                            .header("Authorization", token)
                            .header("Content-Type", "application/json")
                            .asJson();

                    if (resp != null && resp.getStatus() == 200 && resp.getStatusText().equalsIgnoreCase("200") ) {

                        log.info("[ MESSAGE SMS SERVICE ] INFO =========>  SMS ENVOYÉ AVEC SUCCES et enregistrée");
                        sms.setResponseCode(resp.getStatus());

                        sms.setResponseSuccessful(true);

                        sms.setExpediteur(sms.getSmsCredential().getSenderId());

                        sms.setDelivery(""+resp.getBody().getObject().get("message"));

                        sms.setActive(true);

                        sms.setCreation(instant);

                        sms.setLastUpdate(instant);

                        sms.setSmsCredential(smsCredentialService.findOne(sms.getSmsCredential().getSenderId()));

                        sms.setServiceCourrier(sms.getServiceCourrier());

                        smsEnvoyeRepository.save(sms);

                        log.info("[ MESSAGE SMS SERVICE ] CONTENU =========>  <{}>", sms);
                    }
                }
            }
            else{

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType,
                        "username=" + sms.getSmsCredential().getUsername() + "" +
                                "&password=" + sms.getSmsCredential().getPassword()  + "" +
                                "&sender_id=" + sms.getSmsCredential().getSenderId() + "" +
                                "&phone=" + sms.getNumeroDestinataire() + "" +
                                "&message=" + sms.getCorpsMessage() + "");
                //log.info("[ MESSAGE SMS SERVICE ] INFO =========>  SMS CONSTRUIT");
                Request request = new Request.Builder()
                        .url(Objects.requireNonNull(env.getProperty("api.sms.url")))
                        .post(body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("Accept", "*/*")
                        .addHeader("Host", "vavasms.com")
                        .build();

                log.info("[ MESSAGE SMS SERVICE ] INFO =========>  ENVOI DE L'SMS");
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {

                    log.info("[ MESSAGE SMS SERVICE ] INFO =========>  SMS ENVOYÉ AVEC SUCCES et enregistrée");

                    sms.setResponseCode(response.code());

                    sms.setResponseSuccessful(response.isSuccessful());

                    sms.setExpediteur(sms.getSmsCredential().getSenderId());

                    sms.setDelivery("Your message has been received");

                    sms.setActive(true);

                    sms.setCreation(instant);

                    sms.setLastUpdate(instant);

                    sms.setSmsCredential(smsCredentialService.findOne(sms.getSmsCredential().getSenderId()));

                    sms.setServiceCourrier(sms.getServiceCourrier());

                    smsEnvoyeRepository.save(sms);

                    log.info("[ MESSAGE SMS SERVICE ] CONTENU =========>  <{}>", sms.toString());
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void smsAuthorization (SmsEnvoye sms) throws UnirestException {

        try {

            Instant instant = Instant.now();

            String gateway      = Objects.requireNonNull(env.getProperty("api.sms.gateway"));
            String bearer       = Objects.requireNonNull(env.getProperty("credential.sms.bearer"));
            String senderId     = Objects.requireNonNull(env.getProperty("credential.sms.sender"));

            log.info("[ MESSAGE SMS SERVICE ] GATE-WAY SELECTED ---------------- -------------  <{}>", gateway);

            if(gateway != null && !gateway.isEmpty() && gateway.equalsIgnoreCase("LETEXTO")){

                String token            = "Bearer "+bearer;
                String sender           = ""+senderId;
                String campaignName     = "ENVOI APPLICATION "+senderId;
                String campaignType     = "SIMPLE";
                String recipientSource  = "CUSTOM";
                String destination      = "NAT";
                String Message          = ""+sms.getCorpsMessage();
                String phone            = ""+sms.getNumeroDestinataire();

                String requestObject = "{  \"step\": null,  \"sender\": \""+sender+"\""
                        + ",  \"name\": \""+campaignName+"\""
                        + ",  \"campaignType\": \""+campaignType+"\",  \"recipientSource\": \""+recipientSource+"\""
                        + ",  \"groupId\": null"
                        + ",  \"filename\": null,  \"saveAsModel\": false"
                        + ",  \"destination\": \""+destination+"\""
                        + ",  \"message\": \""+Message+"\",  \"emailText\": null"
                        + ",  \"recipients\": [{\"phone\": \""+phone+"\"}]"
                        + ",  \"sendAt\": []"
                        + ",  \"dlrUrl\": \"http://dlr.my.domain.com\""
                        + ", \"responseUrl\": \"http://res.my.domain.com\"}";

                Unirest.setTimeouts(0, 0);
                HttpResponse<JsonNode> response = Unirest.post("http://api.letexto.com/v1/campaigns")
                        .header("Authorization", token)
                        .header("Content-Type", "application/json")
                        .body(requestObject)
                        .asJson();

                String id = (String) response.getBody().getObject().get("id");

                if(id != null && response.getStatus() == 200 && response.getStatusText().equalsIgnoreCase("200")){

                    HttpResponse<JsonNode> resp = Unirest.post("http://api.letexto.com/v1/campaigns/"+id+"/schedules")
                            .header("Authorization", token)
                            .header("Content-Type", "application/json")
                            .asJson();

                    if (resp != null && resp.getStatus() == 200 && resp.getStatusText().equalsIgnoreCase("200") ) {

                        log.info("[ MESSAGE SMS SERVICE ] INFO =========>  SMS ENVOYÉ AVEC SUCCES et enregistrée");
                        sms.setResponseCode(resp.getStatus());

                        sms.setResponseSuccessful(true);

                        sms.setExpediteur(senderId);

                        sms.setDelivery(""+resp.getBody().getObject().get("message"));

                        sms.setActive(true);

                        sms.setCreation(instant);

                        sms.setLastUpdate(instant);

                        smsEnvoyeRepository.save(sms);

                        log.info("[ MESSAGE SMS SERVICE ] CONTENU =========>  <{}>", sms);
                    }
                }
            }
            else{

                log.info("API non configuré.");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public void smsSendToDigitalWeb (SmsEnvoye sms) throws UnirestException {

        try {

            Instant instant = Instant.now();

            String gateway      = Objects.requireNonNull(env.getProperty("api.sms.gateway"));
            String bearer       = Objects.requireNonNull(env.getProperty("credential.sms.bearer"));
            String senderId     = Objects.requireNonNull(env.getProperty("credential.sms.digital"));

            log.info("[ MESSAGE SMS SERVICE ] GATE-WAY SELECTED ---------------- -------------  <{}>", gateway);

            if(gateway != null && gateway.equalsIgnoreCase("LETEXTO")){

                String token            = "Bearer "+bearer;
                String sender           = ""+senderId;
                String campaignName     = "ENVOI APPLICATION "+senderId;
                String campaignType     = "SIMPLE";
                String recipientSource  = "CUSTOM";
                String destination      = "NAT";
                String Message          = ""+sms.getCorpsMessage();
                String phone            = ""+sms.getNumeroDestinataire();

                String requestObject = "{  \"step\": null,  \"sender\": \""+sender+"\""
                        + ",  \"name\": \""+campaignName+"\""
                        + ",  \"campaignType\": \""+campaignType+"\",  \"recipientSource\": \""+recipientSource+"\""
                        + ",  \"groupId\": null"
                        + ",  \"filename\": null,  \"saveAsModel\": false"
                        + ",  \"destination\": \""+destination+"\""
                        + ",  \"message\": \""+Message+"\",  \"emailText\": null"
                        + ",  \"recipients\": [{\"phone\": \""+phone+"\"}]"
                        + ",  \"sendAt\": []"
                        + ",  \"dlrUrl\": \"http://dlr.my.domain.com\""
                        + ", \"responseUrl\": \"http://res.my.domain.com\"}";

                Unirest.setTimeouts(0, 0);
                HttpResponse<JsonNode> response = Unirest.post("http://api.letexto.com/v1/campaigns")
                        .header("Authorization", token)
                        .header("Content-Type", "application/json")
                        .body(requestObject)
                        .asJson();

                String id = (String) response.getBody().getObject().get("id");

                if(id != null && response.getStatus() == 200 && response.getStatusText().equalsIgnoreCase("200")){

                    HttpResponse<JsonNode> resp = Unirest.post("http://api.letexto.com/v1/campaigns/"+id+"/schedules")
                            .header("Authorization", token)
                            .header("Content-Type", "application/json")
                            .asJson();

                    if (resp != null && resp.getStatus() == 200 && resp.getStatusText().equalsIgnoreCase("200") ) {

                        log.info("[ MESSAGE SMS SERVICE ] INFO =========>  SMS ENVOYÉ AVEC SUCCES et enregistrée");
                        sms.setResponseCode(resp.getStatus());

                        sms.setResponseSuccessful(true);

                        sms.setExpediteur(senderId);

                        sms.setDelivery(""+resp.getBody().getObject().get("message"));

                        sms.setActive(true);

                        sms.setCreation(instant);

                        sms.setLastUpdate(instant);

                        smsEnvoyeRepository.save(sms);

                        log.info("[ MESSAGE SMS SERVICE ] CONTENU =========>  <{}>", sms);
                    }
                }
            }
            else{

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType,
                        "username=supportdev@phoenixacces.com" +
                                "&password=P@ssword$92"+
                                "&sender_id=DIGITAL WEB" +
                                "&phone=" + sms.getNumeroDestinataire() + "" +
                                "&message=" + sms.getCorpsMessage() + "");


                Request request = new Request.Builder()
                        .url(Objects.requireNonNull(env.getProperty("api.sms.url")))
                        .post(body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("Accept", "*/*")
                        .addHeader("Host", "vavasms.com")
                        .build();

                log.info("[ MESSAGE SMS SERVICE ] INFO =========>  ENVOI DE L'SMS");
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {

                    log.info("[ MESSAGE SMS SERVICE ] INFO =========>  SMS ENVOYÉ AVEC SUCCES et enregistrée");

                    sms.setResponseCode(response.code());

                    sms.setResponseSuccessful(response.isSuccessful());

                    sms.setExpediteur("PHOENIX LTD");

                    sms.setDelivery("Your message has been received");

                    sms.setActive(true);

                    sms.setCreation(instant);

                    sms.setLastUpdate(instant);

                    sms.setSmsCredential(smsCredentialService.findOne("DIGITAL WEB"));

                    sms.setServiceCourrier(sms.getServiceCourrier());

                    smsEnvoyeRepository.save(sms);

                    log.info("[ MESSAGE SMS SERVICE ] CONTENU =========>  <{}>", sms.toString());
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
