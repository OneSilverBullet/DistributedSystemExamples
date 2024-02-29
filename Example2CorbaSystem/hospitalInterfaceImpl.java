import hospitalObj.*;


public class hospitalInterfaceImpl extends hospitalInterfacePOA {
    @Override
    public void message() {

    }

    @Override
    public boolean AddAppointment(String appointmentID, short appointmentType, short capacity) {
        return false;
    }

    @Override
    public boolean RemoveAppointment(String appointmentID, short appointmentType) {
        return false;
    }

    @Override
    public String ListAppointmentAvailability(short AppointmentType) {
        return null;
    }

    @Override
    public boolean BookAppointment(String patientID, String appointmentID, short appointmentType) {
        return false;
    }

    @Override
    public String GetAppointmentSchedule(String patientID) {
        return null;
    }

    @Override
    public String GetAppointmentScheduleLocal(String patientID) {
        return null;
    }

    @Override
    public boolean CancelAppointment(String patientID, String appointmentID) {
        return false;
    }

    @Override
    public boolean CheckUser(String userID) {
        return false;
    }

    @Override
    public String RegisterUser(short cityType, short userType) {
        return null;
    }
}
