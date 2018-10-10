package com.lzzcms.components;

import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LzzcmsTrustManager implements TrustManager, X509TrustManager {  
    public X509Certificate[] getAcceptedIssuers() {  
        return null;  
    }  
    public void checkServerTrusted(X509Certificate[] certs, String authType) {  
           
    }  
    public void checkClientTrusted(X509Certificate[] certs, String authType) {  
            
    }  
}  