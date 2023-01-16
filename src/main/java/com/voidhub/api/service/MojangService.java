package com.voidhub.api.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;

@Service
public class MojangService {

    public boolean isExistingMinecraftUser(String minecraftUsername) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + minecraftUsername);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            return con.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

}
