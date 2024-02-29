import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public interface CenterObject extends Remote {

    public String RegisterUser(Type.CityType cityT, Type.UserType userT) throws NotBoundException, RemoteException;
    public boolean CheckUser(Type.CityType city, String userID) throws NotBoundException, RemoteException ;

    public boolean BookAppointment(Type.CityType city, String userid_, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException;

    public boolean CancelAppointment(Type.CityType city, String userID, String apID) throws NotBoundException, RemoteException;

    public HashMap<String, Type.AppointmentType> GetAppointmentSchedule(Type.CityType city, String userID) throws NotBoundException, RemoteException;

    public boolean addAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type, int capacity) throws NotBoundException, RemoteException;

    public boolean RemoveAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException;

    public ConcurrentHashMap<String, Integer> ListAppointmentAvailability(Type.CityType city, Type.AppointmentType type) throws NotBoundException, RemoteException;
}

