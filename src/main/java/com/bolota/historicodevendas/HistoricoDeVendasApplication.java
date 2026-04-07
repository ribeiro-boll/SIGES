package com.bolota.historicodevendas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class HistoricoDeVendasApplication {
    public static String getLocalIpFast() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();  // ex: 192.168.0.50
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
    public static void main(String[] args) {
        String ip = getLocalIpFast();

        System.out.println("SIEGES aberto no endereço: http://" + ip + ":" + 8080);
        SpringApplication.run(HistoricoDeVendasApplication.class, args);
    }
}
