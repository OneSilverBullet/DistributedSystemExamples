import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIHospitalServerMtl {
    public static void main(String args[]){
        //RMI methods
        try {
            String adminObjectName = "AdminObject";
            AdminObject adminObject = new Admin();
            AdminObject adminStub = (AdminObject) UnicastRemoteObject.exportObject(adminObject, 0);

            String patientObjectName = "PatientObject";
            PatientObject patientObject = new Patient();
            PatientObject patientStub = (PatientObject) UnicastRemoteObject.exportObject(patientObject, 0);

            String commonObjectName = "CommonObject";
            ServerOpObject commonObject = new ServerOp();
            ServerOpObject commonStub = (ServerOpObject) UnicastRemoteObject.exportObject(commonObject, 0);


            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("rmi://localhost:1099/" + adminObjectName, adminStub);
            registry.rebind("rmi://localhost:1099/" + patientObjectName, patientStub);
            registry.rebind("rmi://localhost:1099/" + commonObjectName, commonStub);

            HospitalServer.cityType = Type.CityType.MTL;

            //start the udp server
            DatagramSocket aSocket = new DatagramSocket(6000);
            byte[] buffer = new byte[1000];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String res = new String(buffer);
                res = res.substring(0,1);
                System.out.println("received:" + res);
                //process request
                if(res.equals("1")){
                    System.out.println("get 1!");
                    String marshallingInfor = HospitalServer.getInstance().MarshallingAppointmentAvaliableLocal();
                    System.out.println("Sent Data:" + marshallingInfor);
                    DatagramPacket reply = new DatagramPacket(marshallingInfor.getBytes(), marshallingInfor.length(),
                            request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
            }
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }

    }
}
