import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import hospitalObj.hospitalInterfaceHelper;
import hospitalObj.hospitalInterface;

public class ServerHospitalMTL {
    public static void main(String args[]){

        HospitalServer.cityType = Type.CityType.MTL;

        // Start CORBA server in one thread
        Thread corbaThread = new Thread(() -> {
            String[] initArgs = {"-ORBInitialPort", "1055", "-ORBInitialHost", "localhost"};
            try {
                ORB orb = ORB.init(initArgs, null);
                POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
                rootpoa.the_POAManager().activate();

                // create servant and register it with the ORB
                hospitalInterfaceImpl hospitalInf = new hospitalInterfaceImpl();
                hospitalInf.setORB(orb);

                // get object reference from the servant
                org.omg.CORBA.Object ref = rootpoa.servant_to_reference(hospitalInf);
                hospitalInterface href = (hospitalInterface)hospitalInterfaceHelper.narrow(ref);

                org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
                NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

                NameComponent path[] = ncRef.to_name( "HospitalServerMTL" );
                ncRef.rebind(path, href);

                System.out.println("Server Run now !! ...");

                // wait for invocations from clients
                orb.run();
            } catch (WrongPolicy e) {
                throw new RuntimeException(e);
            } catch (InvalidName e) {
                throw new RuntimeException(e);
            } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
                throw new RuntimeException(e);
            } catch (AdapterInactive e) {
                throw new RuntimeException(e);
            } catch (ServantNotActive e) {
                throw new RuntimeException(e);
            } catch (CannotProceed e) {
                throw new RuntimeException(e);
            } catch (NotFound e) {
                throw new RuntimeException(e);
            }
        });
        corbaThread.start();

        // Start UDP server in another thread
        Thread udpThread = new Thread(() -> {
            try{
                DatagramSocket aSocket = new DatagramSocket(6000);
                byte[] buffer = new byte[1000];
                while(true){
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request);
                    String res = new String(buffer);
                    String operationBit = res.substring(0,1);
                    System.out.println("received:" + res);
                    String returnValue = "";

                    //process request
                    if(operationBit.equals("1")){
                        System.out.println("get 1!");
                        returnValue = HospitalServer.getInstance().MarshallingAppointmentAvaliableLocal();
                    }
                    else if(operationBit.equals("2")){ //CheckAppointmentAvailableUDP
                        System.out.println("get 2!");
                        returnValue = String.valueOf(HospitalServer.getInstance().CheckAppointmentAvailableLocal(res));
                    }
                    else if(operationBit.equals("3")){ //CheckAppointmentExistUDP
                        System.out.println("get 3!");
                        returnValue = String.valueOf(HospitalServer.getInstance().CheckAppointmentBookedLocal(res));
                    }
                    else if(operationBit.equals("4")){ //BookAppointmentUDP
                        System.out.println("get 4!");
                        returnValue = String.valueOf(HospitalServer.getInstance().BookAppointmentUDPProcess(res));
                    }
                    else if(operationBit.equals("5")){ //CancelAppointmentUDP
                        System.out.println("get 5!");
                        returnValue = String.valueOf(HospitalServer.getInstance().CancelAppointmentUDPProcess(res));
                    }

                    System.out.println("Sent Data:" + returnValue);
                    DatagramPacket reply = new DatagramPacket(returnValue.getBytes(), returnValue.length(),
                            request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
            }
            catch (Exception e) {
                System.err.println("ComputeEngine exception:");
                e.printStackTrace();
            }
        });
        udpThread.start();
    }
}
