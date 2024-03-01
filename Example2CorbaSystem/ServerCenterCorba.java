import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import centerObj.centerInterfaceHelper;
import centerObj.centerInterface;

public class ServerCenterCorba {
    public static void main(String args[]){
        String[] initArgs = {"-ORBInitialPort", "1055", "-ORBInitialHost", "localhost"};

        try {
            ORB orb = ORB.init(initArgs, null);
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            centerInterfaceImpl centerINF=new centerInterfaceImpl();
            centerINF.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(centerINF);
            centerInterface href = (centerInterface)centerInterfaceHelper.narrow(ref);

            org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name( "CenterServer" );
            ncRef.rebind(path, href);

            System.out.println("Server Run now !! ...");

            // wait for invocations from clients
            orb.run();
        } catch (WrongPolicy e) {
            throw new RuntimeException(e);
        } catch (InvalidName e) {
            throw new RuntimeException(e);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
            throw new RuntimeException(e);
        } catch (AdapterInactive e) {
            throw new RuntimeException(e);
        } catch (ServantNotActive e) {
            throw new RuntimeException(e);
        } catch (CannotProceed e) {
            throw new RuntimeException(e);
        } catch (NotFound e) {
            throw new RuntimeException(e);
        }

    }
}
