import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface PatientObject extends Remote {
    public boolean bookAppointment(String patientID, String appointmentID, Type.AppointmentType type)
            throws RemoteException, NotBoundException;

    public HashMap<String, Type.AppointmentType> getAppointmentSchedule(String patientID)
            throws RemoteException, NotBoundException;
    public HashMap<String, Type.AppointmentType> getAppointmentScheduleLocal(String patientID)
            throws RemoteException, NotBoundException;

    public boolean cancelAppointment(String patientID, String appointmentID)
            throws RemoteException, NotBoundException;
}
