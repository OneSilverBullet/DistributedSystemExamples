import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import centerObj.centerInterfaceHelper;
import centerObj.centerInterface;

public class CorbaServer {
    public static void main(String[] args){
        try{

            String[] initArgs = {"-ORBInitialPort", "1055", "-ORBInitialHost", "localhost"};

            ORB orb = ORB.init(initArgs, null);
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            centerInterfaceImpl centerINF=new centerInterfaceImpl();
            centerINF.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(centerINF);
            centerInterface href = centerInterfaceHelper.narrow(ref);

            org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name( "CenterServer" );
            ncRef.rebind(path, href);

            System.out.println("Server Run now !! ...");

            // wait for invocations from clients
            for (;;){
                System.out.println("OBR Programming Run!");
                orb.run();
            }
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }

}
