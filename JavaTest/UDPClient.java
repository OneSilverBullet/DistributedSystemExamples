import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import centerObj.tp;
import centerObj.tpHelper;

public class UDPClient {
    public static void main(String args[]){

        DatagramSocket aSocket = null;
        try {
            String[] initArgs = {"-ORBInitialPort", "1055", "-ORBInitialHost", "localhost"};
            ORB orb = ORB.init(initArgs, null);
            org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            tp addobj = (tp) tpHelper.narrow(ncRef.resolve_str("ABC"));
            //System.out.println("Welcome to system:");
            addobj.message();

            aSocket = new DatagramSocket();
            String m = "1";
            InetAddress aHost = InetAddress.getByName("127.0.0.1");
            int serverPort = 6789;
            DatagramPacket request =
                    new DatagramPacket(m.getBytes(),  m.length(), aHost, serverPort);
            aSocket.send(request);
            System.out.println("Sent");
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            System.out.println("Received");
            String responseStr = new String(reply.getData());

            System.out.println("message from server: " + responseStr);

            int startIndex = 0;

        } catch (InvalidName e) {
            throw new RuntimeException(e);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
            throw new RuntimeException(e);
        } catch (CannotProceed e) {
            throw new RuntimeException(e);
        } catch (NotFound e) {
            throw new RuntimeException(e);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(aSocket != null) aSocket.close();
        }
    }
}