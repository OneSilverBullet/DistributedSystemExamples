import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;


public class Patient implements PatientObject {
    Patient(){
        super();
    }

    public boolean bookAppointment(String patientID, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return HospitalServer.getInstance().BookAppointment(patientID, appointmentID, type);
    }

    public HashMap<String, Type.AppointmentType> getAppointmentSchedule(String patientID) throws NotBoundException, RemoteException {
        return HospitalServer.getInstance().GetAppointmentSchedule(patientID);
    }

    public HashMap<String, Type.AppointmentType> getAppointmentScheduleLocal(String patientID)
            throws RemoteException, NotBoundException {
        return HospitalServer.getInstance().GetAppointmentScheduleLocal(patientID);
    }

    public boolean cancelAppointment(String patientID, String appointmentID) throws NotBoundException, RemoteException {

        return HospitalServer.getInstance().CancelAppointment(patientID, appointmentID);
    }
}
