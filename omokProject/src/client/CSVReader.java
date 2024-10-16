package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
    public static void main(String[] args) {
        String csvFile = "src/client/weather.csv";  // CSV 파일 경로
        String line;
        String cvsSplitBy = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                String nx = data[0];  // 첫 번째 컬럼 (nx 좌표)
                String ny = data[1];  // 두 번째 컬럼 (ny 좌표)
                
                // 좌표 출력
                System.out.println("nx: " + nx + ", ny: " + ny);
                
                // API 호출 부분에 nx, ny 값 사용
                // 여기서 WeatherAPI 메서드를 호출하여 API 요청 수행 가능
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
