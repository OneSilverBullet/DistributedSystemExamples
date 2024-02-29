import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class UDPServer{
    public static void main(String args[]){ 
    	DatagramSocket aSocket = null;
		try{
	    	aSocket = new DatagramSocket(6789);
					// create socket at agreed port
			byte[] buffer = new byte[1000];
 			while(true){
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);
				String res = new String(buffer);
				res = res.substring(0,10);
				System.out.println(res);
				String response = "Derederder";
    			DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(),
    				request.getAddress(), request.getPort());
    			aSocket.send(reply);
    		}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
    }
}
