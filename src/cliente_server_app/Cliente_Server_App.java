/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_server_app;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 * @author davidlorente
 */
public class Cliente_Server_App{

    /**
     * @param args the command line arguments
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException;
     *
     */
    public static void main(String[] args) throws UnknownHostException, IOException {

        int port = 5555;
        String ip = "localhost";
        int packets = 3;
        int size = 30;
        Client client = new Client(ip, port, packets, size);
        client.start();
        client.sendPackets();

    }
    

}
