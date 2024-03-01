import hospitalObj.*;
import org.omg.CORBA.ORB;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class hospitalInterfaceImpl extends hospitalInterfacePOA {
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public void message() {

    }

    @Override
    public boolean AddAppointment(String appointmentID, short appointmentType, short capacity) {
        try{
            return HospitalServer.getInstance().AddAppointment(appointmentID, Type.AppointmentType.values()[appointmentType], capacity);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean RemoveAppointment(String appointmentID, short appointmentType) {
        try{
            return HospitalServer.getInstance().RemoveAppointment(appointmentID, Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String ListAppointmentAvailability(short AppointmentType) {
        try{
            ConcurrentHashMap<String, Integer> res = HospitalServer.getInstance().ListAppointmentAvailability(Type.AppointmentType.values()[AppointmentType]);
            System.out.println("ths concurrent map size:" + res.size());
            return Type.MarshallingHashMap(res);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean BookAppointment(String patientID, String appointmentID, short appointmentType) {
        try{
            return HospitalServer.getInstance().BookAppointment(patientID, appointmentID, Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String GetAppointmentSchedule(String patientID) {
        try{
            HashMap<String, Type.AppointmentType> res = HospitalServer.getInstance().GetAppointmentSchedule(patientID);
            return Type.MarshallingAppointmentsAndType(res);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String GetAppointmentScheduleLocal(String patientID) {
        try{
            HashMap<String, Type.AppointmentType> res = HospitalServer.getInstance().GetAppointmentScheduleLocal(patientID);
            return Type.MarshallingAppointmentsAndType(res);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean CancelAppointment(String patientID, String appointmentID) {
        try{
            return HospitalServer.getInstance().CancelAppointment(patientID, appointmentID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean CheckUser(String userID) {
        try{
            return HospitalServer.getInstance().CheckUser(userID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String RegisterUser(short cityType, short userType) {
        try{
            return HospitalServer.getInstance().RegisterUser(Type.CityType.values()[cityType], Type.UserType.values()[userType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
