package shared;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class Multicast {

    private String MULTICAST_ADDRESS;;
    private int PORT;
    protected MulticastSocket socket = null;
    private InetAddress group;

    public Multicast(String multicast_address, int port) {
        this.PORT = port;
        this.MULTICAST_ADDRESS = multicast_address;
        try {
            socket = new MulticastSocket(PORT); // create socket without binding it (no PORT) if only for sending
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        try {
            byte[] buffer = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
            
            byte[] buffer2 = new byte[1000000];
            DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length);
            socket.receive(packet2);
        }catch(SocketTimeoutException s){
            System.out.println("Timed out after 15 seconds");
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        try {
            byte[] buffer = new byte[1000000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
            String msg = new String(packet.getData(), 0, packet.getLength());
            System.out.println(msg);
            return msg;
        }catch(SocketTimeoutException s){
                System.out.println("Timed out after 15 seconds");
                return "Insucess";
        
        }catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public void closeSocket() {
        socket.close();
    }
}