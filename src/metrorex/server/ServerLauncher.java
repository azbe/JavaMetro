
package metrorex.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class ServerLauncher 
{
    public final static String PORT_FILEPATH = "server_port.txt";
    public final static int BACKUP_PORT = 12449;
    
    public static void main (String[] args)
    {
        try 
        (   
            FileInputStream fin = new FileInputStream(PORT_FILEPATH);
            Scanner in = new Scanner(fin); 
            Server server = new Server(in.nextInt());
        ) 
        {
            new Thread(() -> 
            {
                Scanner stop = new Scanner(System.in);
                stop.nextInt();
                try 
                {
                    server.stop();
                } 
                catch (IOException ex) 
                {
                    System.out.println("Error closing server: " + ex.getMessage());
                }
                return;
            }).start();
            server.open();
        }
        catch (Exception ex) 
        {
            System.out.println("Error opening server: " + ex.getMessage());
            ex.printStackTrace(System.out);
            System.out.println("Attempting backup port: " + BACKUP_PORT);
            
            try (Server server = new Server(BACKUP_PORT)) 
            {
                new Thread(() -> 
                {
                    Scanner stop = new Scanner(System.in);
                    stop.nextInt();
                    try 
                    {
                        server.stop();
                    } 
                    catch (IOException ex1) 
                    {
                        System.out.println("Error closing server: " + ex1.getMessage());
                    }
                    return;
                }).start();
                server.open();
            }
            catch (Exception ex1)
            {
                System.out.println("FATAL ERROR - Could not open server: " + ex1.getMessage());
                ex1.printStackTrace(System.out);
            }
        }
    }
}
