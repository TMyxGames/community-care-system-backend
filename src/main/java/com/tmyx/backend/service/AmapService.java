package com.tmyx.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AmapService {
    private final String KEY = "9b1a1582da2ce7c8da2ac5514af38674";
    private final String GEO_URL = "https://restapi.amap.com/v3/geocode/geo?address={address}&key={key}";

    public double[] getCoordinate(String address) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // 发送get请求
            JsonNode response = restTemplate.getForObject(GEO_URL, JsonNode.class, address, KEY);

            if (response != null && "1".equals(response.get("status").asText())) {
                JsonNode geocodes = response.get("geocodes");
                if (geocodes.isArray() && geocodes.size() > 0) {
                    String location = geocodes.get(0).get("location").asText();
                    String[] coords = location.split(",");
                    return new double[]{Double.parseDouble(coords[0]), Double.parseDouble(coords[1])};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
