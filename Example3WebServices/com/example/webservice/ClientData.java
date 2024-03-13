package com.example.webservice;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class ClientData {
    private static ClientData instance = null;

    public String userID;

    public Type.UserType userType;
    public Type.CityType cityType;
    public Integer index;

    public  Center centralPlatform;


    public static ClientData getInstance() throws NotBoundException, RemoteException {
        if(instance == null){
            instance = new ClientData();
        }
        return instance;
    }

    private ClientData() {
        try {
            URL url = new URL("http://localhost:8080/center?wsdl");
            QName qName = new QName("http://webservice.example.com/", "CenterImplService");
            Service service = Service.create(url, qName);
            centralPlatform = service.getPort(Center.class);
        }
        catch (Exception e) {
            System.out.println("Client exception: " + e);
            e.printStackTrace();
        }
    }


    public class ClientLogSystem{
        void WriteStr(String v) throws IOException {
            try{
                Calendar cal = Calendar.getInstance();
                int date = cal.get(Calendar.DATE);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                String currentTime = String.valueOf(hour) + ":" + String.valueOf(minute) + " " +  String.valueOf(date) + "." + String.valueOf(month) + "." + String.valueOf(year);

                BufferedWriter out = new BufferedWriter(new FileWriter(userID + ".txt",true));
                out.write(v + "  Time:" + currentTime + " \n");
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ClientLogSystem clientLog = new ClientLogSystem();

    public void Initialize(String ID){
        this.userID = ID;
        Type.UserEntity entity = new Type.UserEntity();
        entity.DeserializeUser(ID);
        userType = entity.user;
        cityType = entity.city;
        index = entity.index;
    }

    public void Initialize(String cityValue_, String userType_) throws RemoteException, NotBoundException {
        Type.CityType cityT = Type.CityType.valueOf(cityValue_);
        Type.UserType userT = Type.UserType.valueOf(userType_);
        userType = userT;
        cityType = cityT;
        userID = centralPlatform.RegisterUser((short)cityT.ordinal(), (short)userT.ordinal());
        Type.UserEntity entity = new Type.UserEntity();
        entity.DeserializeUser(userID);
        index = entity.index;
    }

    public boolean IsPatient(){
        return this.userType == Type.UserType.P;
    }

    public boolean IsValidUserName(String userID) throws RemoteException, NotBoundException {
        Type.UserEntity userEntity1 = new Type.UserEntity();
        userEntity1.DeserializeUser(userID);
        return centralPlatform.CheckUser((short)userEntity1.city.ordinal(), userID);
    }

    public boolean BookAppointment(String userid_, String city_, String time_, String date_, String month_, String year_, String type_) throws RemoteException, NotBoundException {
        String appointmentID = city_ + time_ + date_ + month_ + year_;
        boolean res = centralPlatform.BookAppointment((short)cityType.ordinal(), userid_, appointmentID, (short)Type.AppointmentType.valueOf(type_).ordinal());
        try{
            clientLog.WriteStr("Book Appointment Operation ID:" + appointmentID + " appointment Type:" + type_ + " Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public short SwapAppointment(String cityType, String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType)
    {

        return centralPlatform.SwapAppointment((short)Type.CityType.valueOf(cityType).ordinal(), patientID, oldAppointmentID, (short)Type.AppointmentType.valueOf(oldAppointmentType).ordinal(),
                newAppointmentID, (short)Type.AppointmentType.valueOf(newAppointmentType).ordinal());
    }

    public boolean CancelAppointment(String appointmentID) throws RemoteException, NotBoundException {
        boolean res = centralPlatform.CancelAppointment((short)cityType.ordinal(), userID, appointmentID);
        try{
            clientLog.WriteStr("Cancel Appointment Operation ID:" + appointmentID + " Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public String[] ViewBookedAppointments() throws RemoteException, InterruptedException, NotBoundException {
        String rawRes = centralPlatform.GetAppointmentSchedule((short)cityType.ordinal(), userID);
        HashMap<String, Type.AppointmentType> res = Type.UnmarshallingAppointmentsAndType(rawRes);

        String[] ret = new String[res.size()];
        int index = 0;
        for(String key : res.keySet()){
            String value = res.get(key).toString();
            ret[index] = "Appointment ID:" + key + " captegory:" + value;
            index++;
        }
        try{
            clientLog.WriteStr("View Booked Appointment Operation ID");
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return ret;
    }

    public boolean AddAppointment(String city_, String time_, String date_, String month_, String year_, String type_, int capacity) throws RemoteException, NotBoundException {
        String appointmentID = city_ + time_ + date_ + month_ + year_;
        boolean res = centralPlatform.AddAppointment((short)cityType.ordinal(), appointmentID, (short)Type.AppointmentType.valueOf(type_).ordinal(), (short)capacity);
        try{
            clientLog.WriteStr("Add Appointment Operation ID:" + appointmentID + " appointment Type:" + type_ +  " Capacity:" + capacity + "  Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public boolean RemoveAppointment(String appointmentID, String type_) throws RemoteException, NotBoundException {
        boolean res = centralPlatform.RemoveAppointment((short)cityType.ordinal(), appointmentID, (short)Type.AppointmentType.valueOf(type_).ordinal());
        try{
            clientLog.WriteStr("Remove Appointment Operation ID:" + appointmentID + " appointment Type:" + type_ +  "  Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public String[] ViewAvailableAppointments() throws RemoteException, InterruptedException, NotBoundException {
        String rawValiableRes = centralPlatform.ListAppointmentAvailability((short)cityType.ordinal(), (short)(Type.AppointmentType.PHYS.ordinal()));
        System.out.println("rawValiableRes:" + rawValiableRes);
        HashMap<String, Integer> valiableRes = Type.UnmarshallingHashMapForListAvailableAppointments(rawValiableRes);

        String[] ret = new String[valiableRes.size()];
        int index = 0;
        for(String key : valiableRes.keySet()){
            String value = valiableRes.get(key).toString();
            ret[index] = "Appointment:" + key + "  Free Capacity:" + value;
            index++;
        }

        try{
            clientLog.WriteStr("View Available Appointments");
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return ret;
    }
}
