package ru.netology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

        System.out.println(nasa);

        String fileUrl = nasa.getUrl();
        String fileName = Paths.get(nasa.getUrl()).getFileName().toString();

        request = new HttpGet(fileUrl);
        response = httpClient.execute(request);
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        savingFileToDisc(fileUrl, fileName);






    }

    private static void savingFileToDisc(String fileUrl, String fileName) throws IOException {
        //Чтобы прочитать файл, мы будем использовать метод openStream() для получения InputStream
        // Для записи байтов, считанных с URL-адреса, в ваш локальный файл
        // мы будем использовать метод write() из класса FileOutputStream :

        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileUrl)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // handle exception
        }
        InputStream in = new URL(fileUrl).openStream();
        Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
    }
}
