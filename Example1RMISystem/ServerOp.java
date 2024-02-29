import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ServerOp implements ServerOpObject{
    public boolean CheckUser(String userID) throws NotBoundException, RemoteException {
        return HospitalServer.getInstance().CheckUser(userID);
    }

    public String RegisterUser(Type.CityType city, Type.UserType userType) throws NotBoundException, RemoteException {
        return HospitalServer.getInstance().RegisterUser(city, userType);
    }
}
