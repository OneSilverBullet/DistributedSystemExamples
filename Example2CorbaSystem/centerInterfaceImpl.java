import centerObj.*;
import org.omg.CORBA.ORB;

public class centerInterfaceImpl extends centerInterfacePOA {
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public void message() {

    }

    @Override
    public String RegisterUser(short cityType, short userType) {
        return null;
    }

    @Override
    public boolean CheckUser(short city, String userID) {
        return false;
    }

    @Override
    public boolean BookAppointment(short city, String userID, String appointmentID, short appointmentType) {
        return false;
    }

    @Override
    public boolean CancelAppointment(short city, String userID, String appointmentID) {
        return false;
    }

    @Override
    public String GetAppointmentSchedule(short city, String userID) {
        return null;
    }

    @Override
    public boolean AddAppointment(short cityType, String appointmentID, short appointmentType, short capacity) {
        return false;
    }

    @Override
    public boolean RemoveAppointment(short cityType, String appointmentID, short appointmentType) {
        return false;
    }

    @Override
    public String ListAppointmentAvailability(short cityType, short appointmentType) {
        return null;
    }
}
