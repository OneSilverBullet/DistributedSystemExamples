package com.example.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;


@WebService
@SOAPBinding(style= SOAPBinding.Style.RPC)
public interface Center {
    String sayHello(String name);

    String RegisterUser(short cityType, short userType);

    boolean CheckUser(short city, String userID);

    boolean BookAppointment(short city, String userID, String appointmentID, short appointmentType);

    boolean CancelAppointment(short city, String userID, String appointmentID);

    String GetAppointmentSchedule(short city, String userID);

    short SwapAppointment(short CityType, String patientID, String oldAppointmentID, short oldAppointmentType, String newAppointmentID, short newAppointmentType);

    boolean AddAppointment(short cityType, String appointmentID, short appointmentType, short capacity);

    boolean RemoveAppointment(short cityType, String appointmentID, short appointmentType);

    String ListAppointmentAvailability(short cityType, short appointmentType);
}
