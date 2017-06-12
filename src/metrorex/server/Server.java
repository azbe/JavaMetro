package metrorex.server;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metrorex.data.*;
import metrorex.requests.*;

public class Server implements Closeable
{
    private final ServerSocket server;
    
    private Database database;
    
    private final List<Socket> clients;
    private final List<Boolean> clientStatus;
    private final List<ObjectOutputStream> clientOutputStreams;
    private final List<ObjectInputStream> clientInputStreams;
   
    private boolean online;
   
    public Server(int port) throws IOException, FileNotFoundException, ClassNotFoundException
    {
        try
        {
            database = new Database();
        }
        catch (ClassNotFoundException | SQLException ex)
        {
            if (database != null) database.close();
            System.out.println("FATAL ERROR - Could not establish Database connection: " + ex.getMessage());
            ex.printStackTrace(System.out);
            return;
        }
        
        server = new ServerSocket(port);
        
        clients = new ArrayList<Socket>();
        clientStatus = new ArrayList<Boolean>();
        clientOutputStreams = new ArrayList<ObjectOutputStream>();
        clientInputStreams = new ArrayList<ObjectInputStream>();
    }
    
    public void open() throws IOException
    {
        online = true;
        while (online)
        {
            newConnection();
            new Thread(() -> 
            {
                try 
                {
                    startListening();
                } 
                catch (IOException ex) 
                {
                    System.out.println("Error with some connection: " + ex.getMessage());
                }
            }).start();
        }
    }
    
    @Override
    public void close() throws IOException
    {
        database.close();
        stop();
    }
    
    protected void newConnection() throws IOException
    {
        Socket newClient = server.accept();
        ObjectOutputStream oos = new ObjectOutputStream(newClient.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(newClient.getInputStream());
        
        clients.add(newClient);
        clientOutputStreams.add(oos);
        clientInputStreams.add(ois);
    }
    
    protected final void startListening() throws IOException
    {
        int index = clients.size() - 1;
        System.out.println("Started communicating with Client #" + (index + 1));
        while (online && listen(index));
        System.out.println("Stopped communicating with Client #" + (index + 1));
        clients.get(index).close();
        clientOutputStreams.get(index).close();
        clientInputStreams.get(index).close();
    }
    
    protected boolean listen(int index)
    {
        try
        {
            Object obj = clientInputStreams.get(index).readObject();
            if (obj instanceof AdditionRequest)
            {
                AdditionRequest ar = (AdditionRequest) obj;
                System.out.println("New AdditionRequest from Client #" + index + ": " + ar.X + " - " + ar.Y + " '" + ar.Z + "'");
                database.add(ar.X, ar.Y, ar.Z);
                clientOutputStreams.get(index).writeObject("Adaugarea a fost realizata cu succes.");
                clientOutputStreams.get(index).reset();
            }
            else if (obj instanceof ValidationRequest)
            {
                ValidationRequest vr = (ValidationRequest) obj;
                System.out.println("New ValidationRequest from Client #" + index + ": " + vr.serie);
                clientOutputStreams.get(index).writeObject(database.validate(vr.serie));
                clientOutputStreams.get(index).reset();
            }
            else if (obj instanceof VerificationRequest)
            {
                VerificationRequest vr = (VerificationRequest) obj;
                System.out.println("New VerificationRequest from Client #" + index + ": " + vr.serie + " " + vr.tip);
                switch(vr.tip)
                {
                    case ALL:
                    {
                        List<Cartela> result = database.get();
                        clientOutputStreams.get(index).writeObject(result);
                        clientOutputStreams.get(index).reset();
                        break;
                    }
                    case SPECIFIC:
                    {
                        Cartela result = database.get(vr.serie);
                        clientOutputStreams.get(index).writeObject(result);
                        clientOutputStreams.get(index).reset();
                        break;
                    }
                }
            }
            else if (obj instanceof String)
                return false;
            else
                throw new ClassNotFoundException();
            return true;
        }
        catch (SQLException sqlex)
        {
            try 
            {
                clientOutputStreams.get(index).writeObject(sqlex);
                clientOutputStreams.get(index).reset();
            } 
            catch (IOException ex) 
            {
                System.out.println("Error sending SQLException to client: " + ex.getMessage());
            }
            finally 
            {
                return true;
            }
        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.out.println("Error communicating with Client #" + (index+1) + ": " + ex.getMessage());
            return false;
        }
    }
    
    protected void stop() throws IOException
    {
        online = false;
        Socket falseClient;
        if (!server.getInetAddress().toString().equals("0.0.0.0/0.0.0.0")) 
            falseClient = new Socket(server.getInetAddress().toString(), server.getLocalPort());
        else
            falseClient = new Socket("localhost", server.getLocalPort());
        
        for (int index = 0; index < clients.size(); index++)
        {
            clients.get(index).close();
            clientOutputStreams.get(index).close();
            clientInputStreams.get(index).close();
        }
        
        server.close();
    }
   
}
