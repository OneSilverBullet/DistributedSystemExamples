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
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import hospitalObj.hospitalInterface;
import hospitalObj.hospitalInterfaceHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;


public class CentralServer {
    private static CentralServer instance = null;
    private Lock lock = new ReentrantLock();
    private ConcurrentHashMap<Type.CityType, Integer> hospitalServerList = new ConcurrentHashMap<Type.CityType, Integer>();
    private ConcurrentHashMap<Type.CityType, hospitalInterface> cityOperations = new ConcurrentHashMap<Type.CityType, hospitalInterface>();

    //Server is a singleton
    public static CentralServer getInstance() throws NotBoundException, RemoteException {
        if(instance == null){
            instance = new CentralServer();
        }
        return instance;
    }

    public hospitalInterface GetCityOpr(Type.CityType c){ return cityOperations.get(c); }

    public String RegisterUser(Type.CityType cityT, Type.UserType userT) throws NotBoundException, RemoteException {
        return GetCityOpr(cityT).RegisterUser((short)cityT.ordinal(), (short)userT.ordinal());
    }

    public boolean CheckUser(Type.CityType city, String userID) throws NotBoundException, RemoteException {
        return GetCityOpr(city).CheckUser(userID);
    }

    public boolean BookAppointment(Type.CityType city, String userid_, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).BookAppointment(userid_, appointmentID, (short)type.ordinal());
    }

    public boolean CancelAppointment(Type.CityType city, String userID, String apID) throws NotBoundException, RemoteException {
        return GetCityOpr(city).CancelAppointment(userID, apID);
    }

    public String GetAppointmentScheduleMarshalling(Type.CityType cityType, String userID){
        return GetCityOpr(cityType).GetAppointmentSchedule(userID);
    }

    public short SwapAppointment(String cityType, String patientID, String oldAppointmentID,
                                 short oldAppointmentType, String newAppointmentID, short newAppointmentType)
    {
        return GetCityOpr(Type.CityType.valueOf(cityType)).SwapAppointment(patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
    }

    public HashMap<String, Type.AppointmentType> GetAppointmentSchedule(Type.CityType city, String userID) throws NotBoundException, RemoteException {
        String rawRes = GetCityOpr(city).GetAppointmentSchedule(userID);
        return Type.UnmarshallingAppointmentsAndType(rawRes);
    }

    public boolean addAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type, int capacity) throws NotBoundException, RemoteException {
        return GetCityOpr(city).AddAppointment(appointmentID, (short)type.ordinal(), (short)capacity);
    }

    public boolean RemoveAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).RemoveAppointment(appointmentID, (short)type.ordinal());
    }

    public String ListAppointmentAvailabilityMarshalling(Type.CityType city, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).ListAppointmentAvailability((short)type.ordinal());
    }

    ConcurrentHashMap<String, Integer> ListAppointmentAvailability(Type.CityType city, Type.AppointmentType type) throws NotBoundException, RemoteException {
        String rawRes = GetCityOpr(city).ListAppointmentAvailability((short)type.ordinal());
        return Type.UnmarshallingConcurrentHashMap(rawRes);
    }

    private hospitalInterface GainHospitalInterface(String portStr, String localStr, String name) throws InvalidName, CannotProceed, NotFound, org.omg.CORBA.ORBPackage.InvalidName {
        //String[] initArgs = {"-ORBInitialPort", "1055", "-ORBInitialHost", "localhost"};
        String[] initArgs = {"-ORBInitialPort", portStr, "-ORBInitialHost", localStr};
        ORB orb = ORB.init(initArgs, null);
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        hospitalInterface hospitalobj = (hospitalInterface) hospitalInterfaceHelper.narrow(ncRef.resolve_str(name));
        return hospitalobj;
    }

    public void Initialize()  {
        hospitalServerList.put(Type.CityType.MTL, 1099);
        hospitalServerList.put(Type.CityType.QUE, 1100);
        hospitalServerList.put(Type.CityType.SHE, 1101);

        String portMtl = "1055";
        String portQue = "1055";
        String portShe = "1055";
        String hostStr = "localhost";

        try{
            hospitalInterface interfaceMtl = GainHospitalInterface(portMtl, hostStr, "HospitalServerMTL");
            hospitalInterface interfaceQue = GainHospitalInterface(portQue, hostStr, "HospitalServerQUE");
            hospitalInterface interfaceShe = GainHospitalInterface(portShe, hostStr, "HospitalServerSHE");
            cityOperations.put(Type.CityType.MTL, interfaceMtl);
            cityOperations.put(Type.CityType.QUE, interfaceQue);
            cityOperations.put(Type.CityType.SHE, interfaceShe);
        }
        catch (InvalidName e) {
            throw new RuntimeException(e);
        } catch (org.omg.CORBA.ORBPackage.InvalidName e) {
            throw new RuntimeException(e);
        } catch (CannotProceed e) {
            throw new RuntimeException(e);
        } catch (NotFound e) {
            throw new RuntimeException(e);
        }
    }

    private CentralServer() throws NotBoundException, RemoteException {
        Initialize();
    }

}
