package openlab.radiohealthdialoguespush;

/**
 * Created by nkk27 on 25/06/15.
 */


public class DBConnection {


    public static void main(String Args[])
    {

        System.out.println("-------- MySQL JDBC Connection Demo ------------");
        try
        {
            Class.forName("com.mysql.jdbc.Driver");//loading the driver
        }
        catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found !!");
            return;
        }
        System.out.println("MySQL JDBC Driver Registered!");



    }

}