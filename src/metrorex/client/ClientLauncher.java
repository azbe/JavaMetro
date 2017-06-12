
package metrorex.client;

import java.io.FileInputStream;
import java.util.Scanner;

public class ClientLauncher 
{
    public final static String CONNECTION_FILEPATH = "client_ipport.txt";
    
    public static void main (String[] args)
    {
        try
        (
            FileInputStream fin = new FileInputStream(CONNECTION_FILEPATH);
            Scanner in = new Scanner(fin);
            
        ) 
        {
            Client client = new Client(in.nextLine(), in.nextInt());
        }
        catch (Exception ex)
        {
            System.out.println("FATAL ERROR: could not connect to server.");
            return;
        }
    }
}
