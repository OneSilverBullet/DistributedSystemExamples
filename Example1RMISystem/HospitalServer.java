import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


public class HospitalServer {
    public class LogSystem
    {
        public String fileName = "";
        void WriteStr(String v) throws IOException{
            try{
                Calendar cal = Calendar.getInstance();
                int date = cal.get(Calendar.DATE);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                String currentTime = String.valueOf(hour) + ":" + String.valueOf(minute) + " " +  String.valueOf(date) + "." + String.valueOf(month) + "." + String.valueOf(year);

                BufferedWriter out = new BufferedWriter(new FileWriter(fileName,true));
                out.write(v + " operation time:" + currentTime + " \n");
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static public Type.CityType cityType;
    public LogSystem logSystem = new LogSystem();
    public ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>> serverData = new ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>>();
    //UserID appointment record
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Type.AppointmentType>> bookingRecord = new ConcurrentHashMap<String, ConcurrentHashMap<String, Type.AppointmentType>>();
    private static HospitalServer instance = null;
    public AtomicInteger userIndex = new AtomicInteger();
    private ConcurrentHashMap<String, String> registedUser = new ConcurrentHashMap<String, String>();
    private Lock lock = new ReentrantLock();

    private ConcurrentHashMap<Type.CityType, AdminObject> cityAdminsOperations = new ConcurrentHashMap<Type.CityType, AdminObject>();
    private ConcurrentHashMap<Type.CityType, PatientObject> cityPatientsOperations = new ConcurrentHashMap<Type.CityType, PatientObject>();

    private boolean isInitializeServerConnection = false;

    //Server is a singleton
    public static HospitalServer getInstance() throws NotBoundException, RemoteException {
        if(instance == null){
            instance = new HospitalServer();
        }
        return instance;
    }

    private HospitalServer() throws NotBoundException, RemoteException {
        serverData.put(Type.AppointmentType.DENTAL, new ConcurrentHashMap<String, Integer>());
        serverData.put(Type.AppointmentType.PHYSICIAN, new ConcurrentHashMap<String, Integer>());
        serverData.put(Type.AppointmentType.SURGEON, new ConcurrentHashMap<String, Integer>());
        InitializeServerConnection();
        try{
            InitializeFileSystem(HospitalServer.cityType);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public boolean InitializeServerConnection() throws RemoteException, NotBoundException {
        if(isInitializeServerConnection) return true;
        System.out.println("InitializeServerConnection");
        isInitializeServerConnection = true;
        String adminObjectName = "AdminObject";
        String patientObjectName = "PatientObject";
        //lookup the registry montreal
        Registry registryMTL = LocateRegistry.getRegistry(1099);
        AdminObject adminStubMtl = (AdminObject) registryMTL.lookup("rmi://localhost:1099/" + adminObjectName);
        PatientObject patientStubMtl = (PatientObject) registryMTL.lookup("rmi://localhost:1099/" + patientObjectName);

        //lookup the registry quebec
        Registry registryQUE = LocateRegistry.getRegistry(1100);
        AdminObject adminStubQue = (AdminObject) registryQUE.lookup("rmi://localhost:1100/" + adminObjectName);
        PatientObject patientStubQue = (PatientObject) registryQUE.lookup("rmi://localhost:1100/" + patientObjectName);

        //lookup the registry she
        Registry registrySHE = LocateRegistry.getRegistry(1101);
        AdminObject adminStubShe = (AdminObject) registrySHE.lookup("rmi://localhost:1101/" + adminObjectName);
        PatientObject patientStubShe = (PatientObject) registrySHE.lookup("rmi://localhost:1101/" + patientObjectName);

        cityAdminsOperations.put(Type.CityType.MTL, adminStubMtl);
        cityAdminsOperations.put(Type.CityType.QUE, adminStubQue);
        cityAdminsOperations.put(Type.CityType.SHE, adminStubShe);
        cityPatientsOperations.put(Type.CityType.MTL, patientStubMtl);
        cityPatientsOperations.put(Type.CityType.QUE, patientStubQue);
        cityPatientsOperations.put(Type.CityType.SHE, patientStubShe);
        return true;
    }

    public boolean InitializeFileSystem(Type.CityType cityType_) throws IOException {
        this.cityType = cityType_;
        String logFileName = "Server"+ cityType.toString()+".txt";
        logSystem.fileName = logFileName;
        return true;
    }
    public String RegisterUser(Type.CityType city, Type.UserType userType) throws NotBoundException, RemoteException {
        if(!isInitializeServerConnection){
            InitializeServerConnection();
        }
        Integer newUser = userIndex.getAndIncrement();
        Type.UserEntity entity = new Type.UserEntity();
        entity.user = userType;
        entity.city = city;
        entity.index = newUser;
        String newUserID = entity.SerializeUser();
        registedUser.put(newUserID, "some client information");
        return newUserID;
    }
    public boolean CheckUser(String userID) throws NotBoundException, RemoteException {
        if(!isInitializeServerConnection){
            InitializeServerConnection();
        }
        if(registedUser.containsKey(userID))
            return true;
        return false;
    }
    public boolean AddAppointment(String appointmentID, Type.AppointmentType type, int capacity) {
        try
        {
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            lock.lock();
            if(serverData.get(type).containsKey(appointmentID))
                return false;
            HospitalServer.getInstance().serverData.get(type).put(appointmentID, capacity);
            System.out.println("Add Availiable Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " Capacity:" + String.valueOf(capacity));
            logSystem.WriteStr("Add Availiable Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " Capacity:" + String.valueOf(capacity) + " Result:true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean RemoveAppointment(String appointmentID, Type.AppointmentType type){
        try{
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            lock.lock();
            HospitalServer.getInstance().serverData.get(type).remove(appointmentID);
            //todo:redirect the appointment

            System.out.println("Remove Availiable Appointment:" + appointmentID + " Appointment Type:" + type.toString());
            logSystem.WriteStr("Remove Availiable Appointment:" + appointmentID + " Appointment Type:" + type.toString()  + " Result:true");
        }
        catch (IOException e){
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return true;
    }

    private String MarshallingHashMap(HashMap<String, Integer> v){
        int a = v.size();
        String ret = Type.AlignStr(String.valueOf(a), 4);
        for(String item : v.keySet()){
            Integer value = v.get(item);
            String valueAlStr = Type.AlignStr(String.valueOf(value.intValue()), 4);
            ret += item + valueAlStr;
        }
        return ret;
    }

    public HashMap<String, Integer> ListAppointmentAvailabilityLocal(Type.AppointmentType type){
        HashMap<String, Integer> resObject = new HashMap<String, Integer>();
        for(String key : serverData.get(type).keySet())
        {
            Integer value = serverData.get(type).get(key);
            //current appoint has been full
            if(value > 0)
                resObject.put(key, value);
        }
        return resObject;
    }

    public String MarshallingAppointmentAvaliableLocal(){
        HashMap<String, Integer> dentalApps = ListAppointmentAvailabilityLocal(Type.AppointmentType.DENTAL);
        String dentalPlatStr = MarshallingHashMap(dentalApps);
        HashMap<String, Integer> surgeonApps = ListAppointmentAvailabilityLocal(Type.AppointmentType.SURGEON);
        String surPlatStr = MarshallingHashMap(surgeonApps);
        HashMap<String, Integer> physicalApps = ListAppointmentAvailabilityLocal(Type.AppointmentType.PHYSICIAN);
        String phyPlatStr = MarshallingHashMap(physicalApps);
        return dentalPlatStr + surPlatStr + phyPlatStr;
    }

    class AdminRecordsQuery implements Runnable {
        private int portNum;
        public ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>> queryRecords;
        // constructor
        public AdminRecordsQuery(int portNum, ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>> query_) {
            this.portNum = portNum;
            this.queryRecords = query_;
        }

        int Unmarshalling(String originStr, int startIndex, Type.AppointmentType type){
            System.out.println(originStr);
            String dentalNumStr = originStr.substring(startIndex, startIndex + 4);
            startIndex = startIndex + 4;
            System.out.println("number:" + dentalNumStr + "\n");
            Integer dentalNum = Integer.valueOf(dentalNumStr);
            for(int i = 0; i < dentalNum; ++i){
                String appointmentID = originStr.substring(startIndex, startIndex + 10);
                System.out.println("appID:" + appointmentID + "\n");
                startIndex = startIndex + 10;
                String capacityNum = originStr.substring(startIndex, startIndex + 4);
                System.out.println("capa:" + capacityNum + "\n");
                startIndex = startIndex + 4;
               queryRecords.get(type).put(appointmentID, Integer.valueOf(capacityNum));
            }
            return startIndex;
        }

        public void run() {
            DatagramSocket aSocket = null;
            try {
                aSocket = new DatagramSocket();
                String m = "1";
                InetAddress aHost = InetAddress.getByName("127.0.0.1");
                int serverPort = this.portNum;
                DatagramPacket request =
                        new DatagramPacket(m.getBytes(),  m.length(), aHost, serverPort);
                aSocket.send(request);
                System.out.println("Sent");
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                System.out.println("Received");
                String responseStr = new String(reply.getData());
                int startIndex = 0;

                startIndex = Unmarshalling(responseStr, startIndex, Type.AppointmentType.DENTAL);
                startIndex = Unmarshalling(responseStr, startIndex, Type.AppointmentType.SURGEON);
                startIndex = Unmarshalling(responseStr, startIndex, Type.AppointmentType.PHYSICIAN);

            } catch (RemoteException | SocketException e) {
                throw new RuntimeException(e);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                if(aSocket != null) aSocket.close();
            }
        }
    }


    public ConcurrentHashMap<String, Integer> ListAppointmentAvailability(Type.AppointmentType type)
    {
        ConcurrentHashMap<String, Integer> resObject = new ConcurrentHashMap<String, Integer>();
        try{
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            //multi thread
            int numofHospitals = 3;
            ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>> totalRecord = new ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>>();
            totalRecord.put(Type.AppointmentType.DENTAL, new ConcurrentHashMap<String, Integer>());
            totalRecord.put(Type.AppointmentType.PHYSICIAN, new ConcurrentHashMap<String, Integer>());
            totalRecord.put(Type.AppointmentType.SURGEON, new ConcurrentHashMap<String, Integer>());
            Thread[] threads = new Thread[numofHospitals];

            System.out.println("first build up threads");

            threads[0] = new Thread(new AdminRecordsQuery(6000, totalRecord));
            threads[1] = new Thread(new AdminRecordsQuery(6001, totalRecord));
            threads[2] = new Thread(new AdminRecordsQuery(6002, totalRecord));



            for (int i = 0; i < numofHospitals; i++) {
                threads[i].start();
            }

            System.out.println("start threads");

            for (Thread t : threads) {
                t.join();
            }



            System.out.println("List Availiable Appointment Type:" + type.toString());
            logSystem.WriteStr("List Availiable Appointment Type:" + type.toString()  + " Res:true");
            for(Type.AppointmentType typeKey : totalRecord.keySet())
            {
                ConcurrentHashMap<String, Integer> value = totalRecord.get(typeKey);
                for(String itemKey : value.keySet()){
                    Integer capacity = value.get(itemKey);
                    resObject.put(typeKey.toString() + ":" + itemKey, capacity);
                    logSystem.WriteStr("Appointment:" + typeKey.toString() + ":" + itemKey + "  Capacity:" + value.toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } finally {
            //lock.unlock();
        }
        return resObject;
    }

    public boolean BookAppointment(String patientID, String appointmentID, Type.AppointmentType type){
        //there is no such open appointment
        try
        {
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            lock.lock();
            Type.UserEntity userEnt = new Type.UserEntity();
            userEnt.DeserializeUser(patientID);

            Type.AppointmentEntity entity = new Type.AppointmentEntity();
            entity.DeserializeAppointmentEntity(appointmentID);

            if(entity.city == this.cityType){
                if(!serverData.get(type).containsKey(appointmentID))
                    return false;

                int prev =  serverData.get(type).get(appointmentID).intValue();
                int appointmentSpace = prev;


                //there is no free space
                if(appointmentSpace <= 0)
                    return false;

                if(!bookingRecord.containsKey(patientID))
                    bookingRecord.put(patientID, new ConcurrentHashMap<String, Type.AppointmentType>());
                else{
                    //duplicated record
                    if(bookingRecord.get(patientID).containsKey(appointmentID))
                        return false;

                    //should not go other cities for three times
                    int bookedTimes = 0;
                    for(String key : bookingRecord.get(patientID).keySet()){
                        Type.AppointmentEntity entity1 = new Type.AppointmentEntity();
                        entity1.DeserializeAppointmentEntity(key);
                        if(entity1.city != userEnt.city) bookedTimes = bookedTimes + 1;
                    }
                    if(bookedTimes > 3) return false;
                }

                bookingRecord.get(patientID).put(appointmentID, type);
                appointmentSpace = appointmentSpace - 1;

                serverData.get(type).remove(appointmentID);
                serverData.get(type).put(appointmentID, new Integer(appointmentSpace));
                System.out.println("Book Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " patient:" + patientID);
                logSystem.WriteStr("Book Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " patient:" + patientID  + " Res:true");
            }
            else
            {
                boolean res = cityPatientsOperations.get(entity.city).bookAppointment(patientID, appointmentID, type);
                System.out.println("Book Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " patient:" + patientID  + " Res:"+String.valueOf(res));
                logSystem.WriteStr("Book Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " patient:" + patientID  + " Res:"+String.valueOf(res));
                return res;
            }
        } catch (IOException | NotBoundException e) {
            throw new RuntimeException(e);
        } finally
        {
            lock.unlock();
        }
        return true;
    }

    public boolean CancelAppointment(String patientID, String appointmentID)
    {
        try
        {
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            Type.AppointmentEntity entity = new Type.AppointmentEntity();
            entity.DeserializeAppointmentEntity(appointmentID);
            lock.lock();
            if(entity.city == this.cityType){
                //there is no such record
                if(!(bookingRecord.containsKey(patientID) && bookingRecord.get(patientID).containsKey(appointmentID)))
                    return false;

                //add the appointment
                Type.AppointmentType appType = bookingRecord.get(patientID).get(appointmentID);

                Integer freeSpace = serverData.get(appType).get(appointmentID);
                freeSpace = freeSpace + 1;

                serverData.get(appType).put(appointmentID, freeSpace);
                //cancel appointment
                bookingRecord.get(patientID).remove(appointmentID);

                System.out.println("Cancel Appointment:" + appointmentID + " patient:" + patientID);
                logSystem.WriteStr("Cancel Appointment:" + appointmentID + " patient:" + patientID  + " Res:true");
            }
            else{ //call other city
                boolean res = cityPatientsOperations.get(entity.city).cancelAppointment(patientID, appointmentID);
                logSystem.WriteStr("Remote Process Cancel Appointment:" + appointmentID + " patient:" + patientID  + " Res:" + String.valueOf(res));
            }
        } catch (IOException | NotBoundException e) {
            throw new RuntimeException(e);
        } finally
        {
            lock.unlock();
        }
        return true;
    }
    public HashMap<String, Type.AppointmentType> GetAppointmentScheduleLocal(String patientID){

        System.out.println("Call GetAppointmentScheduleLocal:" + this.cityType.toString());
        HashMap<String, Type.AppointmentType> resObject = new HashMap<String, Type.AppointmentType>();
        if(!bookingRecord.containsKey(patientID))
            return resObject;

        System.out.println("Call GetAppointmentScheduleLocal inside");
        for(String key : bookingRecord.get(patientID).keySet())
        {
            Type.AppointmentType value = bookingRecord.get(patientID).get(key);
            //current appoint has been full
            resObject.put(key, value);
        }
        System.out.println("Res:" + resObject.size());

        return resObject;
    }
    public HashMap<String, Type.AppointmentType> GetAppointmentSchedule(String patientID){
        HashMap<String, Type.AppointmentType> resObject = new HashMap<String, Type.AppointmentType>();
        try{
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            lock.lock();
            //User Multiple RMI methods

            HashMap<String, Type.AppointmentType> resObjectLocalMtl = cityPatientsOperations.get(Type.CityType.MTL).getAppointmentScheduleLocal(patientID);
            HashMap<String, Type.AppointmentType> resObjectLocalQue = cityPatientsOperations.get(Type.CityType.QUE).getAppointmentScheduleLocal(patientID);
            HashMap<String, Type.AppointmentType> resObjectLocalShe = cityPatientsOperations.get(Type.CityType.SHE).getAppointmentScheduleLocal(patientID);

            for(String key : resObjectLocalMtl.keySet())
            {
                Type.AppointmentType value = resObjectLocalMtl.get(key);
                resObject.put(key, value);
            }
            for(String key : resObjectLocalQue.keySet())
            {
                Type.AppointmentType value = resObjectLocalQue.get(key);
                resObject.put(key, value);
            }
            for(String key : resObjectLocalShe.keySet())
            {
                Type.AppointmentType value = resObjectLocalShe.get(key);
                resObject.put(key, value);
            }
            System.out.println("Get Appointment Schedule of patient:" + patientID);
            logSystem.WriteStr("Get Appointment Schedule of patient:" + patientID  + " Res:true");
            for(String key : resObject.keySet())
            {
                Type.AppointmentType value = resObject.get(key);
                //current appoint has been full
                logSystem.WriteStr("Appointment ID:" + key  + " Appointment Type:" + value.toString());
            }
        } catch (IOException | NotBoundException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return resObject;
    }
}
