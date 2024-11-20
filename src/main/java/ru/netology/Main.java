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

        String imageUrl = nasa.getUrl();
        String fileName = Paths.get(nasa.getUrl()).getFileName().toString();


        // Скачиваем изображение
        try {
            downloadImage(imageUrl, fileName);
            System.out.println("Изображение успешно загружено и сохранено как " + fileName);
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке изображения: " + e.getMessage());
        }
    }

    // Метод для загрузки изображения
    private static void downloadImage(String imageUrl, String fileName) throws Exception {
        try (InputStream in = new URL(imageUrl).openStream();
             FileOutputStream out = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }


}








