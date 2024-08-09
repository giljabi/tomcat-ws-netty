package kr.giljabi.gatewaytest;

import kr.giljabi.gatewaytest.runner.ClientComponent;
import kr.giljabi.gatewaytest.runner.ClientRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

/**
 * @Author : eahn.park@gmail.com
 */
@SpringBootApplication
public class ClientTestApplication implements CommandLineRunner {
    private ClientComponent clientComponent;

    private static int numberOfClients;

    @Autowired
    public ClientTestApplication(ClientComponent clientComponent) {
        this.clientComponent = clientComponent;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientTestApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        if(args.length > 0) {
            System.out.println("Number of clients: " + args[0]);
        } else {
            System.out.println("\n================================================");
            System.out.println("Usage: java -jar target\\gateway-client-1.0.jar 1~10");
            System.out.println("================================================");
            return;
        }

        numberOfClients = Integer.parseInt(args[0]);

        for (int i = 0; i < numberOfClients; i++) {
            String username = "admin" + i;
            String password = "qweqwe123";
            String terminalId = String.format("%d", 1000 + i);

            Thread thread = new Thread(new ClientRunner(username,
                    password,
                    terminalId,
                    clientComponent));
            thread.start();

            try {
                Thread.sleep(new Random().nextInt(5000));
                //Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}