import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public interface AdminObject extends Remote {
    public boolean addAppointment(String appointmentID, Type.AppointmentType type,
                        int capacity) throws RemoteException, NotBoundException;

    public boolean removeAppointment(String appointmentID, Type.AppointmentType type)
            throws RemoteException, NotBoundException;

    public ConcurrentHashMap<String, Integer> listAppointmentAvailability(Type.AppointmentType type)
            throws RemoteException, NotBoundException;
}
