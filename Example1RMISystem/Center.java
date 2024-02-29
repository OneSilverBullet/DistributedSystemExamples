import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Center implements CenterObject
{
    public String RegisterUser(Type.CityType cityT, Type.UserType userT) throws NotBoundException, RemoteException {
        return CentralServer.getInstance().RegisterUser(cityT, userT);
    }
    public boolean CheckUser(Type.CityType city, String userID) throws NotBoundException, RemoteException {
        return CentralServer.getInstance().CheckUser(city, userID);
    }

    public boolean BookAppointment(Type.CityType city, String userid_, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return CentralServer.getInstance().BookAppointment(city, userid_, appointmentID, type);
    }

    public boolean CancelAppointment(Type.CityType city, String userID, String apID) throws NotBoundException, RemoteException {
        return CentralServer.getInstance().CancelAppointment(city, userID, apID);
    }

    public HashMap<String, Type.AppointmentType> GetAppointmentSchedule(Type.CityType city, String userID) throws NotBoundException, RemoteException {
        return CentralServer.getInstance().GetAppointmentSchedule(city, userID);
    }

    public boolean addAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type, int capacity) throws NotBoundException, RemoteException {
        return CentralServer.getInstance().addAppointment(city, appointmentID, type, capacity);
    }

    public boolean RemoveAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return CentralServer.getInstance().RemoveAppointment(city, appointmentID, type);
    }

    public ConcurrentHashMap<String, Integer> ListAppointmentAvailability(Type.CityType city, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return CentralServer.getInstance().ListAppointmentAvailability(city, type);
    }
}
