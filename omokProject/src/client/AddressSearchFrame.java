package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class AddressSearchFrame extends JFrame {
    private JTextField addressSearchField;
    private JList<String> addressResultsList;
    private DefaultListModel<String> listModel; // JList에 데이터를 추가하기 위한 모델
    private JButton searchButton;
    private SignUpFrame signUpFrame; // SignUpFrame 인스턴스 저장

    public AddressSearchFrame(SignUpFrame signUpFrame) {
        this.signUpFrame = signUpFrame; // SignUpFrame 인스턴스 저장
        setTitle("주소 검색");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        getContentPane().setLayout(null);

        JLabel lblAddress = new JLabel("주소 입력:");
        lblAddress.setBounds(10, 10, 100, 25);
        getContentPane().add(lblAddress);

        addressSearchField = new JTextField();
        addressSearchField.setBounds(80, 10, 200, 25);
        getContentPane().add(addressSearchField);

        searchButton = new JButton("검색");
        searchButton.setBounds(290, 10, 80, 25);
        getContentPane().add(searchButton);

        listModel = new DefaultListModel<>(); // JList 모델 초기화
        addressResultsList = new JList<>(listModel); // 모델을 사용한 JList 생성
        JScrollPane scrollPane = new JScrollPane(addressResultsList);
        scrollPane.setBounds(10, 45, 360, 180);
        getContentPane().add(scrollPane);

        // 검색 버튼 클릭 이벤트 처리
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = addressSearchField.getText();
                searchAddress(address);
            }
        });

        // JList에서 더블클릭 시 선택된 주소 SignUpFrame으로 전송
        addressResultsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블클릭 감지
                    String selectedText = addressResultsList.getSelectedValue();
                    if (selectedText != null) {
                        // 선택된 리스트 항목에서 우편번호와 도로명 주소를 추출
                        String[] addressParts = selectedText.split("\\|");
                        String zipNo = addressParts[0].trim().split(":")[1].trim(); // 우편번호
                        String rnAdres = addressParts[2].trim().split(":")[1].trim(); // 도로명 주소

                        // SignUpFrame으로 우편번호와 도로명 주소 전달
                        signUpFrame.setAddress(zipNo, rnAdres);
                        dispose(); // 창 닫기
                    }
                }
            }
        });
    }

    // 주소 검색 메서드
    private void searchAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "주소를 입력해주세요.");
            return;
        }

        try {//API URL 구성
            String serviceKey = "D85aH8Ac8zMPg89zw%2BDSXcS2nyVq8spoeLu%2FG8iQTryyPxeQvqQO5vT3UMywDTTOK0BvUEoon1a%2FwCFUl0RkzA%3D%3D";  // 실제 서비스 키로 교체하세요
            StringBuilder urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("srchwrd", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("countPerPage", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("currentPage", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");

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

            // API 응답에서 XML 파싱 시작
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));

            NodeList addressList = document.getElementsByTagName("newAddressListAreaCdSearchAll");

            listModel.clear(); // 이전 검색 결과 초기화

            for (int i = 0; i < addressList.getLength(); i++) {
                String zipNo = addressList.item(i).getChildNodes().item(0).getTextContent();
                String lnmAdres = addressList.item(i).getChildNodes().item(1).getTextContent();
                String rnAdres = addressList.item(i).getChildNodes().item(2).getTextContent();

                // 리스트에 추가할 주소 데이터
                String addressResult = "우편번호: " + zipNo + " | 지번: " + lnmAdres + " | 도로명: " + rnAdres;
                listModel.addElement(addressResult); // 리스트 모델에 추가
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "주소 검색 중 오류가 발생했습니다.");
        }
    }
}
