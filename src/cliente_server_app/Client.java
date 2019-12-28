/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_server_app;

/**
 *
 * @author davidlorente
 */
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Thread {

    private final int port, packets, size;
    private final InetAddress ip;
    private final DatagramSocket socket;

    public Client(String ip, int port, int packets, int size) throws UnknownHostException, IOException {

        this.ip = InetAddress.getByName(ip);
        this.port = port;
        this.size = size;
        this.packets = packets;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(5000);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public int getPort() {
        return port;
    }

    public int getPackets() {
        return packets;
    }

    public int getSize() {
        return size;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void sendPackets() throws IOException {
        DecimalFormat numberFormat = new DecimalFormat("#.0000");
        double d;
        Instant ts2;
        DatagramPacket datagram;
        ByteBuffer msg;
        for (int i = 0; i < this.packets; i++) {
            ts2 = Instant.now();
            d = (double) ts2.getEpochSecond() + (double) ts2.getNano() / 1000_000_000;
            String[] ts = numberFormat.format(d).split(",");
            System.out.println(numberFormat.format(d));
            msg = ByteBuffer.allocate(this.size);
            msg.putInt(i);
            msg.putInt(Integer.parseInt(ts[0]));
            msg.put(ts[1].getBytes());
            msg.putInt(0);
            msg.putInt(0);
            byte[] msg_array = msg.array();
            datagram = new DatagramPacket(msg_array, msg_array.length, ip, port);
            this.socket.send(datagram);
            System.out.println("Packet " + i + " has been sent!");
        }
        System.out.println("All packets have been sent!");
    }

    @Override
    public void run() {
        ArrayList<DatagramPacket> packetList = new ArrayList<>();
        try {
            while (true) {
                byte[] buffer = new byte[this.size];
                DatagramPacket data = new DatagramPacket(buffer, buffer.length);
                this.socket.receive(data);
                packetList.add(data);

            }
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout reached! ");
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(packetList.size());
        ArrayList<Resultados> listResult;

        listResult = this.getData(packetList);
        for (Resultados result : listResult) {
            System.out.println("ID: " + result.getId() + "\nTS_TX: " + result.getTs_tx() + "\nTS_RX: " + result.getTs_rx());
        }
    }

    public ArrayList<Resultados> getData(ArrayList<DatagramPacket> packetList) {
        ArrayList<Resultados> listResult = new ArrayList<>();
        int intts_tx, intts_rx, id;
        String dects_tx, dects_rx;
        byte[] intts_tx_arr = new byte[4];
        byte[] dects_tx_arr = new byte[4];
        byte[] id_arr = new byte[4];
        byte[] intts_rx_arr = new byte[4];
        char[] dects_rx_arr = new char[4];

        for (DatagramPacket packet : packetList) {
            byte[] dataPacket = packet.getData();
            for (int z = 0; z < 20; z++) {
                if (z >= 0 && z <= 3) {
                    id_arr[z] = dataPacket[z];
                }
                if (z >= 4 && z <= 7) {
                    intts_tx_arr[z - 4] = dataPacket[z];
                }
                if (z >= 8 && z <= 11) {
                    dects_tx_arr[z - 8] = dataPacket[z];
                }
                if (z >= 12 && z <= 15) {
                    intts_rx_arr[z - 12] = dataPacket[z];
                }
                if (z >= 16 && z <= 19) {
                    dects_rx_arr[z - 16] = (char) dataPacket[z];
                }
            }
            id = ByteBuffer.wrap(id_arr).getInt();
            intts_tx = ByteBuffer.wrap(intts_tx_arr).getInt();
            dects_tx = new String(dects_tx_arr);
            intts_rx = ByteBuffer.wrap(intts_rx_arr).getInt();
            dects_rx = String.valueOf(dects_rx_arr);
            double ts_tx = buildTs(Integer.toString(intts_tx), dects_tx.substring(0, 3));
            double ts_rx = buildTs(Integer.toString(intts_rx), dects_rx);
            Resultados result = new Resultados(id, ts_tx, ts_rx);
            listResult.add(result);
        }
        return listResult;
    }

//    public static byte[] toBinary(int data) {
//        byte[] result = new byte[4];
//
//        result[0] = (byte) ((data & 0xFF000000) >> 24);
//        result[1] = (byte) ((data & 0x00FF0000) >> 16);
//        result[2] = (byte) ((data & 0x0000FF00) >> 8);
//        result[3] = (byte) data;
//
//        return result;
//    }
//
//    public static int toDecimal(byte[] bytes) {
//        return (bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF));
//    }

    public static Double buildTs(String intts, String dects) {
        String ts = intts + "." + dects;
        return Double.parseDouble(ts);
    }
}
