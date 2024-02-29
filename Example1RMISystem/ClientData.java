import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientData {
    private static ClientData instance = null;

    public String userID;

    public Type.UserType userType;
    public Type.CityType cityType;
    public Integer index;

    public CenterObject centralPlatform;


    public static ClientData getInstance() throws NotBoundException, RemoteException {
        if(instance == null){
            instance = new ClientData();
        }
        return instance;
    }

    private ClientData() throws RemoteException, NotBoundException {
        Registry registryMTL = LocateRegistry.getRegistry(1098);
        centralPlatform = (CenterObject) registryMTL.lookup("rmi://localhost:1098/CentralPlatform");
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
        userID = centralPlatform.RegisterUser(cityT, userT);
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
        return centralPlatform.CheckUser(userEntity1.city, userID);
    }

    public boolean BookAppointment(String userid_, String city_, String time_, String date_, String month_, String year_, String type_) throws RemoteException, NotBoundException {
        String appointmentID = city_ + time_ + date_ + month_ + year_;
        boolean res = centralPlatform.BookAppointment(cityType, userid_, appointmentID, Type.AppointmentType.valueOf(type_));
        try{
            clientLog.WriteStr("Book Appointment Operation ID:" + appointmentID + " appointment Type:" + type_ + " Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public boolean CancelAppointment(String appointmentID) throws RemoteException, NotBoundException {
        boolean res = centralPlatform.CancelAppointment(cityType, userID, appointmentID);
        try{
            clientLog.WriteStr("Cancel Appointment Operation ID:" + appointmentID + " Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public String[] ViewBookedAppointments() throws RemoteException, InterruptedException, NotBoundException {
        HashMap<String, Type.AppointmentType> res = centralPlatform.GetAppointmentSchedule(cityType, userID);

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
        boolean res = centralPlatform.addAppointment(cityType, appointmentID, Type.AppointmentType.valueOf(type_), capacity);
        try{
            clientLog.WriteStr("Add Appointment Operation ID:" + appointmentID + " appointment Type:" + type_ +  " Capacity:" + capacity + "  Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public boolean RemoveAppointment(String appointmentID, String type_) throws RemoteException, NotBoundException {
        boolean res = centralPlatform.RemoveAppointment(cityType, appointmentID, Type.AppointmentType.valueOf(type_));
        try{
            clientLog.WriteStr("Remove Appointment Operation ID:" + appointmentID + " appointment Type:" + type_ +  "  Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public String[] ViewAvailableAppointments() throws RemoteException, InterruptedException, NotBoundException {
        ConcurrentHashMap<String, Integer> valiableRes = centralPlatform.ListAppointmentAvailability(cityType, Type.AppointmentType.PHYSICIAN);

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
