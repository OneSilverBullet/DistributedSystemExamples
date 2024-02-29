import java.util.Calendar;

public class Type {

    public enum AppointmentType {
        PHYSICIAN,
        SURGEON,
        DENTAL,
    }

    public enum CityType
    {
        MTL, //Montreal
        QUE, //Qubec
        SHE, //Sherbrooke
    }

    public enum TimeSlot
    {
        M, //moring
        A, //afternoon
        E, //evening
    }

    public enum UserType
    {
        P, //patient
        A, //admin
    }

    public static String AlignStr(String input, int alignSize)
    {
        String res = "";
        int inputLength = input.length();
        for(int i = inputLength; i < alignSize; ++i){
            res = res + "0";
        }
        res += input;
        return res;
    }

    public static class UserEntity
    {
        public CityType city;
        public UserType user;
        public Integer index = 0;

        public String SerializeUser(){
            String cityStr = this.city.toString();
            String userStr = this.user.toString();
            int indexNum = this.index.toString().length();
            String indexStr = AlignStr(this.index.toString(), 4);
            return cityStr + userStr + indexStr;
        }

        public boolean DeserializeUser(String userEntityStr){
            String cityStr = userEntityStr.substring(0, 3);
            this.city = CityType.valueOf(cityStr);
            String userStr = userEntityStr.substring(3, 4);
            this.user = UserType.valueOf(userStr);
            String indexStr = userEntityStr.substring(4,8);
            this.index = Integer.valueOf(indexStr);
            return true;
        }
    }

    public static class AppointmentEntity
    {
        public CityType city;
        public TimeSlot time;
        public int day;
        public int month;
        public int year;
        public AppointmentEntity(){
            this.city = CityType.MTL;
            this.time = TimeSlot.A;
            this.day = 1;
            this.month = 1;
            this.year = 1;
        }
        public AppointmentEntity(CityType city, TimeSlot ts, int d, int m, int y){
            this.city = city;
            this.time = ts;
            Calendar cal=Calendar.getInstance();
            day = d;
            month = m;
            year = y;
        }

        public String SerializeAppointmentEntity(){
            return city.toString() + time.toString() + AlignStr(String.valueOf(day), 2) +
                    AlignStr(String.valueOf(month), 2) +  String.valueOf(year);
        }

        public boolean DeserializeAppointmentEntity(String appointmentEntity){
            String cityStr = appointmentEntity.substring(0, 3);
            this.city = CityType.valueOf(cityStr);
            String userStr = appointmentEntity.substring(3, 4);
            this.time = TimeSlot.valueOf(userStr);
            this.day = Integer.valueOf(appointmentEntity.substring(4, 6));
            this.month = Integer.valueOf(appointmentEntity.substring(6, 8));
            this.year = Integer.valueOf(appointmentEntity.substring(8, 10));
            return true;
        }
    }

    public static void main(String args[]){
        //test case about base types
        UserEntity userA = new UserEntity();
        userA.city = CityType.QUE;
        userA.user = UserType.P;
        userA.index = 123;
        String idA = userA.SerializeUser();
        System.out.println(idA);
        String idB = "MTLP0008";
        UserEntity userB = new UserEntity();
        userB.DeserializeUser((idB));
        System.out.println(userB.city);
        System.out.println(userB.user);
        System.out.println(userB.index);
        System.out.println(userB.SerializeUser());
        AppointmentEntity appointment = new AppointmentEntity(CityType.MTL, TimeSlot.E, 7,2,24);
        String appointmentStr = appointment.SerializeAppointmentEntity();
        System.out.println(appointmentStr);
        appointment.DeserializeAppointmentEntity("SHEE101124");
        System.out.println(appointment.day);
        System.out.println(appointment.month);
        System.out.println(appointment.year);
        System.out.println(appointment.city);
        System.out.println(appointment.time);
    }
}
