import java.net.*;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import centerObj.centerInterface;

public class CentralServer {
    private static CentralServer instance = null;
    private Lock lock = new ReentrantLock();
    private ConcurrentHashMap<Type.CityType, Integer> hospitalServerList = new ConcurrentHashMap<Type.CityType, Integer>();

    private ConcurrentHashMap<Type.CityType, centerInterface> cityOperations = new ConcurrentHashMap<Type.CityType, centerInterface>();


    //Server is a singleton
    public static CentralServer getInstance() throws NotBoundException, RemoteException {
        if(instance == null){
            instance = new CentralServer();
        }
        return instance;
    }

    public centerInterface GetCityOpr(Type.CityType c){ return cityOperations.get(c); }

    public String RegisterUser(Type.CityType cityT, Type.UserType userT) throws NotBoundException, RemoteException {
        return GetCityOpr(cityT).RegisterUser((short)cityT.ordinal(), (short)userT.ordinal());
    }

    public boolean CheckUser(Type.CityType city, String userID) throws NotBoundException, RemoteException {
        return GetCityOpr(city).CheckUser((short)city.ordinal(), userID);
    }

    public boolean BookAppointment(Type.CityType city, String userid_, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).BookAppointment((short)city.ordinal(), userid_, appointmentID, (short)type.ordinal());
    }

    public boolean CancelAppointment(Type.CityType city, String userID, String apID) throws NotBoundException, RemoteException {
        return GetCityOpr(city).CancelAppointment((short)city.ordinal(), userID, apID);
    }

    public HashMap<String, Type.AppointmentType> GetAppointmentSchedule(Type.CityType city, String userID) throws NotBoundException, RemoteException {
        String rawRes = GetCityOpr(city).GetAppointmentSchedule((short)city.ordinal(), userID);
        HashMap<String, Type.AppointmentType> res= new HashMap<>();
        //todo:marshalling
        return res;
    }

    public boolean addAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type, int capacity) throws NotBoundException, RemoteException {
        return GetCityOpr(city).AddAppointment((short)city.ordinal(), appointmentID, (short)type.ordinal(), (short)capacity);
    }

    public boolean RemoveAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).RemoveAppointment((short)city.ordinal(), appointmentID, (short)type.ordinal());
    }

    ConcurrentHashMap<String, Integer> ListAppointmentAvailability(Type.CityType city, Type.AppointmentType type) throws NotBoundException, RemoteException {
        String rawRes = GetCityOpr(city).ListAppointmentAvailability((short)city.ordinal(), (short)type.ordinal());
        //todo: rawRes
        ConcurrentHashMap<String, Integer> res = new ConcurrentHashMap<>();
        return res;
    }

    public void Initialize() throws RemoteException, NotBoundException {
        hospitalServerList.put(Type.CityType.MTL, 1099);
        hospitalServerList.put(Type.CityType.QUE, 1100);
        hospitalServerList.put(Type.CityType.SHE, 1101);

        //Connect to the hospital servers
        String adminObjectName = "AdminObject";
        String patientObjectName = "PatientObject";
        String commonObjectName = "CommonObject";

        //lookup the registry montreal
        Registry registryMTL = LocateRegistry.getRegistry(1099);
        AdminObject adminStubMtl = (AdminObject) registryMTL.lookup("rmi://localhost:1099/" + adminObjectName);
        PatientObject patientStubMtl = (PatientObject) registryMTL.lookup("rmi://localhost:1099/" + patientObjectName);
        ServerOpObject commonStubMtl = (ServerOpObject) registryMTL.lookup("rmi://localhost:1099/" + commonObjectName);

        //lookup the registry quebec
        Registry registryQUE = LocateRegistry.getRegistry(1100);
        AdminObject adminStubQue = (AdminObject) registryQUE.lookup("rmi://localhost:1100/" + adminObjectName);
        PatientObject patientStubQue = (PatientObject) registryQUE.lookup("rmi://localhost:1100/" + patientObjectName);
        ServerOpObject commonStubQue = (ServerOpObject) registryQUE.lookup("rmi://localhost:1100/" + commonObjectName);

        //lookup the registry she
        Registry registrySHE = LocateRegistry.getRegistry(1101);
        AdminObject adminStubShe = (AdminObject) registrySHE.lookup("rmi://localhost:1101/" + adminObjectName);
        PatientObject patientStubShe = (PatientObject) registrySHE.lookup("rmi://localhost:1101/" + patientObjectName);
        ServerOpObject commonStubShe = (ServerOpObject) registrySHE.lookup("rmi://localhost:1101/" + commonObjectName);

        cityAdminsOperations.put(Type.CityType.MTL, adminStubMtl);
        cityAdminsOperations.put(Type.CityType.QUE, adminStubQue);
        cityAdminsOperations.put(Type.CityType.SHE, adminStubShe);
        cityPatientsOperations.put(Type.CityType.MTL, patientStubMtl);
        cityPatientsOperations.put(Type.CityType.QUE, patientStubQue);
        cityPatientsOperations.put(Type.CityType.SHE, patientStubShe);
        cityCommonOperations.put(Type.CityType.MTL, commonStubMtl);
        cityCommonOperations.put(Type.CityType.QUE, commonStubQue);
        cityCommonOperations.put(Type.CityType.SHE, commonStubShe);
    }

    private CentralServer() throws NotBoundException, RemoteException {
        Initialize();
    }

}
