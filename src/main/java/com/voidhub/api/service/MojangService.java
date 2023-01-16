package com.voidhub.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voidhub.api.entity.MinecraftUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;


@Service
public class MojangService {

    public Optional<MinecraftUserInfo> getMinecraftUserInfo(String minecraftUsername) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + minecraftUsername))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, String> responseMap = mapper.readValue(response.body(), HashMap.class);

                String uuid = addHyphensToUUID(responseMap.get("id"));
                return Optional.of(new MinecraftUserInfo(UUID.fromString(uuid), responseMap.get("name")));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.empty();
    }

    private String addHyphensToUUID(String uuid) {
        return uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        );
    }

}
