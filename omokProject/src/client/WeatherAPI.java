package client;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherAPI {
    private static final String API_KEY = "D85aH8Ac8zMPg89zw%2BDSXcS2nyVq8spoeLu%2FG8iQTryyPxeQvqQO5vT3UMywDTTOK0BvUEoon1a%2FwCFUl0RkzA%3D%3D";
    private static final String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

    // 좌표 (nx, ny)에 대한 날씨 정보를 가져오는 메서드
    public static String getWeatherInfo(String nx, String ny, JLabel weatherLabel, JLabel imageLabel) {
        try {
            // 현재 날짜와 시간을 포맷팅
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
            String currentDate = dateFormat.format(new Date());
            String currentTime = timeFormat.format(new Date());

            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append("?serviceKey=").append(API_KEY);
            urlBuilder.append("&pageNo=1");
            urlBuilder.append("&numOfRows=1000");
            urlBuilder.append("&dataType=JSON");
            urlBuilder.append("&base_date=").append(currentDate); // 현재 날짜
            urlBuilder.append("&base_time=").append(currentTime); // 현재 시간
            urlBuilder.append("&nx=").append(nx); // x 좌표
            urlBuilder.append("&ny=").append(ny); // y 좌표

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

            // API 응답 출력
            //System.out.println("API 응답: " + sb.toString());

            // JSON 파싱
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONObject response = jsonObject.getJSONObject("response");
            String resultCode = response.getJSONObject("header").getString("resultCode");

            // 결과 코드에 따라 처리
            if (resultCode.equals("00")) { // 성공
                JSONObject body = response.getJSONObject("body");
                if (body.has("items")) {
                    JSONObject itemsObject = body.getJSONObject("items");
                    if (itemsObject.has("item")) {
                        JSONArray items = itemsObject.getJSONArray("item");
                        String weatherState = parseWeatherItems(items);
                        
                        // 날씨 상태에 따라 이미지 설정
                        switch (weatherState) {
                            case "맑음":
                                imageLabel.setIcon(new ImageIcon("src/image/sunny.png"));
                                break;
                            case "비":
                                imageLabel.setIcon(new ImageIcon("src/image/rainy.png"));
                                break;
                            case "눈":
                                imageLabel.setIcon(new ImageIcon("src/image/snow.png"));
                                break;
                            case "흐림":
                                imageLabel.setIcon(new ImageIcon("src/image/cloudy.png"));
                                break;
                        }
                        
                        weatherLabel.setText(weatherState);
                        return weatherState; // 날씨 상태 반환
                    } else {
                        return "item 데이터가 응답에 존재하지 않습니다.";
                    }
                } else {
                    return "items가 응답에 존재하지 않습니다.";
                }
            } else {
                return "오류: " + response.getJSONObject("header").getString("resultMsg");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "날씨 정보를 가져오는 중 오류 발생";
        }
    }


    // JSON 배열을 파싱하여 날씨 정보를 반환
    private static String parseWeatherItems(JSONArray items) {
        boolean hasRain = false;
        boolean hasSnow = false;
        boolean isClear = true;

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String category = item.getString("category");
            String obsrValue = item.getString("obsrValue");

            // 날씨 상태 판별
            switch (category) {
                case "PTY": // 강수형태
                    if (obsrValue.equals("0")) {
                        isClear = true; // 맑음
                    } else if (obsrValue.equals("1")) {
                        hasRain = true; // 비
                    } else if (obsrValue.equals("2") || obsrValue.equals("3")) {
                        hasSnow = true; // 눈
                    }
                    break;
            }
        }

        // 결과 조합
        if (hasRain) {
            return "비";
        } else if (hasSnow) {
            return "눈";
        } else if (isClear) {
            return "맑음";
        } else {
            return "흐림"; // 기본값
        }
    }
}
