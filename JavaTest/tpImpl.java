import org.omg.CORBA.ORB;
import centerObj.tpPOA;

public class tpImpl extends tpPOA {
    private ORB orb;

    public void setORB(ORB orb_val) {
        setOrb(orb_val);
    }

    public void message() {
        // TODO Auto-generated method stub

        System.out.println("Hello world!");

    }

    public ORB getOrb() {
        return orb;
    }

    public void setOrb(ORB orb) {
        this.orb = orb;
    }
}
