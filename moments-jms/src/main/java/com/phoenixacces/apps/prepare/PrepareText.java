package com.phoenixacces.apps.prepare;

public class PrepareText {

    public static String content(String replace, String content, String type) {

        content = content.replace(type, replace);

        /*content = content.replace("[demande]", replace);
        content = content.replace("[staut]", replace);
        content = content.replace("[prestationName]", replace);*/
        return content;
        //return content.replace(replace, content);
    }
}
