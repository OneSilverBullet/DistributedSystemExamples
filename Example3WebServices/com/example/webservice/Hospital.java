package com.example.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style= SOAPBinding.Style.RPC)
public interface Hospital {
    String sayHello(String name);

    boolean AddAppointment(String appointmentID, short appointmentType,
                           short capacity);

    boolean RemoveAppointment(String appointmentID, short appointmentType);

    String ListAppointmentAvailability(short AppointmentType);

    boolean BookAppointment(String patientID, String appointmentID,
                            short appointmentType);

    String GetAppointmentSchedule(String patientID);

    String GetAppointmentScheduleLocal(String patientID);

    boolean CancelAppointment(String patientID, String appointmentID);

    boolean CheckUser(String userID);

    String RegisterUser(short cityType, short userType);

    short SwapAppointment(String patientID, String oldAppointmentID, short oldAppointmentType, String newAppointmentID, short newAppointmentType);
}
