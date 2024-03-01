import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientA {
    public static void main(String[] args) throws NotBoundException, RemoteException {
        ClientData.corbaPort = "1055";
        ClientData.corbaHost = "localhost";
        new UserInterface("Distributed Health Care Management System (DHMS) Client A");
    }
}
