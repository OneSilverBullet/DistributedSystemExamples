import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CenterRMIServer {
    public static void main(String args[]){
        //RMI methods
        try {
            String centralObjectName = "CentralPlatform";
            CenterObject centralObj = new Center();
           // centralObj.Initialize();
            CenterObject centralStub = (CenterObject) UnicastRemoteObject.exportObject(centralObj, 0);

            //create central platform
            Registry registry = LocateRegistry.createRegistry(1098);
            registry.rebind("rmi://localhost:1098/" + centralObjectName, centralStub);
            System.out.println("ComputeEngine bound");


        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }

    }
}
