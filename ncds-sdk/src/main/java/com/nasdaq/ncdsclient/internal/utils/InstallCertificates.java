package com.nasdaq.ncdsclient.internal.utils;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.x509.*;
import org.json.JSONObject;
import sun.security.x509.URIName;
import sun.security.x509.X509CertImpl;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;


public class InstallCertificates {
    private final CloseableHttpClient httpClient;
    private final String streamsCertPath;
    private final String trustStorePath;
    private final String keyStorePassword;
    private String oneTimeUrl;
    private final String authUrl = "http://clouddataservice.auth.nasdaq.com/";
    private final String kafkaCertUrl="https://clouddataservice.nasdaq.com/api/v1/get-certificate";

    public InstallCertificates (String keyStorePath, String password) {
        this.httpClient = HttpClients.createDefault();
        this.streamsCertPath = keyStorePath + File.separator + "streams.crt";
        this.trustStorePath = keyStorePath + File.separator +"ncdsTrustStore.p12";
        this.keyStorePassword = password;
    }

    public void install() throws Exception {
        installCertsToTrustStore(keyStorePassword);
    }

    private void downloadCertificates() throws Exception {
        try {
            URL certificateURL = new URL(getStreamsCertificate());
            FileUtils.copyURLToFile(certificateURL, new File(this.streamsCertPath));
        }catch (Exception e){
            System.out.println("Error Downloading Certificate");
            throw (e);
        }
    }

    private String getStreamsCertificate() throws Exception {
        HttpGet request = new HttpGet(kafkaCertUrl);
        request.setHeader(new BasicHeader("Prama", "no-cache"));
        request.setHeader(new BasicHeader("Cache-Control", "no-cache"));
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            if(response.getStatusLine().getStatusCode()!= 200){
                throw (new Exception("Internal Server Error"));
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {

                String result = EntityUtils.toString(entity);
                JSONObject obj = new JSONObject(result);
                oneTimeUrl = obj.get("one_time_url").toString();
            }
            this.httpClient.close();
        }
        return oneTimeUrl;
    }

    private void createKeyStore(String password) throws Exception {
        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12"); //X.509, PKCS12
            File file = new File(trustStorePath);

            if (!file.exists()) {
                keystore.load(null, password.toCharArray());
                try (FileOutputStream fos = new FileOutputStream(trustStorePath)) {
                    keystore.store(fos, password.toCharArray());
                }
            }
        }catch (Exception e){
            System.out.println("Error Creating Key Store");
            throw (e);
        }
    }

    private void installCertsToTrustStore(String password) throws Exception {
        try {
            //Download Kafka Certificates
            downloadCertificates();

            //create Keystore if it doesnt exists
            createKeyStore(password);

            KeyStore keystore = KeyStore.getInstance("PKCS12"); //X.509, PKCS12

            //Loading keystore
            keystore.load(new FileInputStream(trustStorePath), password.toCharArray());

            // Opening streams certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(new FileInputStream(streamsCertPath));

            //adding streams certificate
            keystore.setCertificateEntry("streams", cert);

            // Adding auth certificate
            keystore.setCertificateEntry("auth", getAuthCertificate() );

            try (FileOutputStream fos = new FileOutputStream(trustStorePath)) {
                keystore.store(fos, password.toCharArray());
            }
        } catch (Exception e){
            System.out.println("Error installing Certificate");
            throw (e);
        }

    }

    private Certificate getAuthCertificate() throws Exception {
        URL url = new URL(null, authUrl, new sun.net.www.protocol.https.Handler());
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.connect();
        Certificate[] certs = con.getServerCertificates();
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        for(Certificate cert : certs) {
            List<sun.security.x509.AccessDescription> descriptions = ((X509CertImpl) cert).getAuthorityInfoAccessExtension().getAccessDescriptions();
            for (sun.security.x509.AccessDescription ad : descriptions) {
                // check if it's a URL to issuer's certificate
                if (ad.getAccessMethod().toString().equals(X509ObjectIdentifiers.id_ad_caIssuers.toString())) {
                    sun.security.x509.GeneralName location = ad.getAccessLocation();
                    if (location.getType() == GeneralName.uniformResourceIdentifier) {
                        // Get issuer's URL
                        String issuerUrl = ((URIName) location.getName()).getURI().toString();
                        URL url1 = new URL(issuerUrl);
                        X509Certificate issuer = (X509Certificate) certificateFactory.generateCertificate(url1.openStream());
                        return issuer;
                    }
                }
            }
        }
        return null;
    }
}
