package com.phoenixacces.apps.jms.utilities;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Converter {

    public static <T> String pojoToJson(T pojo) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            //Convert object to JSON string
            json = mapper.writeValueAsString(pojo);

        } catch (JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return json;
        }
    }

    public static <T> T jsonToPojo(String json, Class<T> tClass) {

        ObjectMapper mapper = new ObjectMapper();

        T pojo = null;

        try {
            // Convert JSON string to Object
            pojo = mapper.readValue(json, tClass);

        } catch (JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return pojo;
        }
    }
}

