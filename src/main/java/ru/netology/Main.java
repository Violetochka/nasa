package ru.netology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class Main {

    public static final String REMOTE_API_NASA = "https://api.nasa.gov/planetary/apod?api_key=MhEZuwqPXHrXe4choKkoflSZCWQJ5OPcRbB6V7Mj";
    public static final ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) throws IOException {


        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_API_NASA);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response = httpClient.execute(request);

        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        //String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        //System.out.println(body);


        Nasa nasa = mapper.readValue(response.getEntity().getContent(), Nasa.class);

        HttpGet secondRequest = new HttpGet(nasa.getUrl());
        CloseableHttpResponse secondResponse = httpClient.execute(secondRequest);
        byte[] bytes = secondResponse.getEntity().getContent().readAllBytes();
        File file = new File("NasaImage.jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(bytes);
        bos.flush();
        bos.close();







    }


}
