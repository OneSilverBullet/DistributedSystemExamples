package com.example.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@WebService(endpointInterface = "com.example.webservice.Center")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class CenterImpl implements Center {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    @Override
    public String RegisterUser(short cityType, short userType) {
        try{
            System.out.println("invocation in center interfalce impl:RegisterUser");
            return CentralServer.getInstance().RegisterUser(Type.CityType.values()[cityType], Type.UserType.values()[userType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean CheckUser(short city, String userID) {
        try{
            System.out.println("invocation in center interfalce impl:CheckUser");
            return CentralServer.getInstance().CheckUser(Type.CityType.values()[city], userID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean BookAppointment(short city, String userID, String appointmentID, short appointmentType) {
        try{
            return CentralServer.getInstance().BookAppointment(Type.CityType.values()[city], userID, appointmentID,
                    Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean CancelAppointment(short city, String userID, String appointmentID) {
        try{
            return CentralServer.getInstance().CancelAppointment(Type.CityType.values()[city],
                    userID, appointmentID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String GetAppointmentSchedule(short city, String userID) {
        try{
            return CentralServer.getInstance().GetAppointmentScheduleMarshalling(Type.CityType.values()[city],
                    userID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short SwapAppointment(short CityType, String patientID, String oldAppointmentID, short oldAppointmentType, String newAppointmentID, short newAppointmentType) {
        try{
            return CentralServer.getInstance().SwapAppointment(Type.CityType.values()[CityType].toString(), patientID,
                    oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean AddAppointment(short cityType, String appointmentID, short appointmentType, short capacity) {
        try{
            return CentralServer.getInstance().addAppointment(Type.CityType.values()[cityType],
                    appointmentID,
                    Type.AppointmentType.values()[appointmentType],
                    capacity);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean RemoveAppointment(short cityType, String appointmentID, short appointmentType) {
        try{
            return CentralServer.getInstance().RemoveAppointment(Type.CityType.values()[cityType],
                    appointmentID,
                    Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String ListAppointmentAvailability(short cityType, short appointmentType) {
        try{
            return CentralServer.getInstance().ListAppointmentAvailabilityMarshalling(Type.CityType.values()[cityType],
                    Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}