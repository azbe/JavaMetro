
package metrorex.client.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import metrorex.data.TipCartela;
import metrorex.requests.*;
import static metrorex.data.TipCartela.*;

public class AdditionListener implements ActionListener
{
    private JTextField adaugareSeriaX, adaugareSeriaY, adaugareTip;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    public AdditionListener(JTextField seriaXField, JTextField seriaYField, JTextField tipField, ObjectOutputStream outputStream, ObjectInputStream inputStream)
    {
        adaugareSeriaX = seriaXField;
        adaugareSeriaY = seriaYField;
        adaugareTip = tipField;
        oos = outputStream;
        ois = inputStream;
    }

    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        String seriaX = adaugareSeriaX.getText();
        String seriaY = adaugareSeriaY.getText();
        String tip = adaugareTip.getText();
        int X, Y;
        TipCartela Z;
        try
        {
            X = Integer.parseInt(seriaX);
            Y = Integer.parseInt(seriaY);
            if (X <= 0 || Y <= 0) throw new IllegalArgumentException("Seria fiecarei cartele trebuie sa fie numar natural nenul!");
            if ("abonament lunar".equals(tip.toLowerCase()))
                Z = ABONAMENT_LUNAR;
            else if ("abonament zi".equals(tip.toLowerCase()))
                Z = ABONAMENT_ZI;
            else if ("10 calatorii".equals(tip.toLowerCase()))
                Z = CALATORII_10;
            else if ("2 calatorii".equals(tip.toLowerCase()))
                Z = CALATORII_2;
            else
                throw new IllegalArgumentException("Tipurile acceptate de cartele sunt: 10 calatorii, 2 calatorii, abonament lunar, abonament zi!");
        }
        catch (IllegalArgumentException iaex)
        {
            JOptionPane.showMessageDialog(new JFrame(), iaex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try 
        {
            oos.writeObject(new AdditionRequest(X, Y, Z));
            oos.reset();
            
            Object response = ois.readObject();
            if (response instanceof SQLException)
                JOptionPane.showMessageDialog(new JFrame(), ((SQLException)response).getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            else if (response instanceof String)
                JOptionPane.showMessageDialog(new JFrame(), (String)response, "Succes", JOptionPane.PLAIN_MESSAGE);
        } 
        catch (IOException | ClassNotFoundException ex) 
        {
            System.out.println("Error writing addition request: " + ex.getMessage());
        }
    }
    
}
