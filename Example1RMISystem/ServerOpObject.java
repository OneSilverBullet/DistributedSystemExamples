import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface ServerOpObject extends Remote {
        public boolean CheckUser(String userID) throws RemoteException, NotBoundException;

        public String RegisterUser(Type.CityType city, Type.UserType userType)
                throws RemoteException, NotBoundException;
}
