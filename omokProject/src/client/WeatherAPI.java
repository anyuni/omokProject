package client;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WeatherAPI {
    private static final String API_KEY = "D85aH8Ac8zMPg89zw%2BDSXcS2nyVq8spoeLu%2FG8iQTryyPxeQvqQO5vT3UMywDTTOK0BvUEoon1a%2FwCFUl0RkzA%3D%3D";
    private static final String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

    public static String getWeatherInfo(JLabel weatherLabel, JLabel weatherImageLabel) {
        try {
            // 현재 날짜와 시간 가져오기
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            // 날짜와 시간을 API 형식에 맞게 변환
            String baseDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = getBaseTime(currentTime);

            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + API_KEY);
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=1");
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=100");
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=JSON");
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + baseDate); // 현재 날짜 설정
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + baseTime); // 현재 시간 설정
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=60"); // 서울의 nx
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=127"); // 서울의 ny

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            // JSON 파싱
            JSONObject jsonResponse = new JSONObject(sb.toString());
            JSONArray weatherArray = jsonResponse.getJSONObject("response")
                                                 .getJSONObject("body")
                                                 .getJSONObject("items")
                                                 .getJSONArray("item");

            String weather = "";
            double temperature = 0.0, humidity = 0.0, precipitation = 0.0;

            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject item = weatherArray.getJSONObject(i);
                String category = item.getString("category");
                double value = item.getDouble("obsrValue");

                switch (category) {
                    case "T1H": // 기온
                        temperature = value;
                        break;
                    case "RN1": // 강수량
                        precipitation = value;
                        break;
                    case "REH": // 습도
                        humidity = value;
                        break;
                    case "PTY": // 강수형태
                        weather = getWeatherCondition((int) value);
                        break;
                }
            }

            // 날씨 정보 출력
            String weatherInfo = String.format("날씨: %s, 온도: %.1f°C, 습도: %.1f%%, 강수량: %.1fmm", weather, temperature, humidity, precipitation);
            weatherLabel.setText(weatherInfo);
            updateWeatherImage(weather, weatherImageLabel);

            return weather;

        } catch (Exception e) {
            e.printStackTrace();
            weatherLabel.setText("날씨 정보를 불러오지 못했습니다.");
            return null;
        }
    }

    // PTY 코드에 따른 날씨 상태를 반환
    private static String getWeatherCondition(int pty) {
        switch (pty) {
            case 1: return "비";
            case 2: return "비/눈";
            case 3: return "눈";
            case 4: return "소나기";
            default: return "맑음"; // 0: 맑음
        }
    }

    // 날씨에 맞는 이미지를 업데이트
    private static void updateWeatherImage(String weather, JLabel weatherImageLabel) {
        switch (weather) {
            case "맑음":
                weatherImageLabel.setIcon(new ImageIcon(LobbyFrame.class.getResource("/image/sunny.png")));
                break;
            case "비":
                weatherImageLabel.setIcon(new ImageIcon(LobbyFrame.class.getResource("/image/rainy.png")));
                break;
            case "눈":
                weatherImageLabel.setIcon(new ImageIcon(LobbyFrame.class.getResource("/image/snow.png")));
                break;
            case "흐림":
                weatherImageLabel.setIcon(new ImageIcon(LobbyFrame.class.getResource("/image/cloudy.png")));
                break;
            default:
                weatherImageLabel.setIcon(null);
        }
    }

    // 현재 시간에 따른 base_time 설정
    private static String getBaseTime(LocalTime currentTime) {
        // API는 1시간 단위로 10분 간격의 기상정보를 제공합니다.
        int hour = currentTime.getHour();

        if (hour < 2) {
            return "0000";
        } else if (hour < 5) {
            return "0200";
        } else if (hour < 8) {
            return "0500";
        } else if (hour < 11) {
            return "0800";
        } else if (hour < 14) {
            return "1100";
        } else if (hour < 17) {
            return "1400";
        } else if (hour < 20) {
            return "1700";
        } else if (hour < 23) {
            return "2000";
        } else {
            return "2300";
        }
    }
}
