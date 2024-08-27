package kr.giljabi.gatewaytest.runner;

import kr.giljabi.gatewaytest.dto.BoothTokenRequest;
import kr.giljabi.gatewaytest.dto.CommonHeader;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * @Author : eahn.park@gmail.com
 */
@Component
public class LoginService {
    private ClientComponent clientComponent;

    public LoginService(ClientComponent clientComponent) {
        this.clientComponent = clientComponent;
    }

    public CommonHeader getToken(String userid, String password) {
        //System.out.println("Login.getToken wasPort:" + clientComponent.getWasPort());
        String url = "http://localhost:" + clientComponent.getWasPort() + "/api/getToken";
        try {
            BoothTokenRequest requestBoothToken = new BoothTokenRequest();
            requestBoothToken.setUserId(userid);
            requestBoothToken.setPassword(password);
            String jsonRequest = new Gson().toJson(requestBoothToken);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            Gson gson = new Gson();
            CommonHeader commonHeader = gson.fromJson(response.body(), CommonHeader.class);
            System.out.println("Response: " + commonHeader.toString());
            return commonHeader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
