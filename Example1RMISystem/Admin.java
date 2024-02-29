import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
public class Admin implements AdminObject {
    Admin(){
        super();
    }

    public boolean addAppointment(String appointmentID, Type.AppointmentType type,
                                  int capacity) throws NotBoundException, RemoteException {
        return HospitalServer.getInstance().AddAppointment(appointmentID, type, capacity);
    }

    public boolean removeAppointment(String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return HospitalServer.getInstance().RemoveAppointment(appointmentID, type);
    }

    public ConcurrentHashMap<String, Integer> listAppointmentAvailability(Type.AppointmentType type) throws NotBoundException, RemoteException {
        return HospitalServer.getInstance().ListAppointmentAvailability(type);
    }
}
