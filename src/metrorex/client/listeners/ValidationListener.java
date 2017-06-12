
package metrorex.client.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import metrorex.requests.*;

public class ValidationListener implements ActionListener 
{
    JTextField validareSerie;
    JLabel raspuns;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    
    public ValidationListener(JTextField serieField, JLabel label, ObjectOutputStream outputStream, ObjectInputStream inputStream)
    {
        validareSerie = serieField;
        raspuns = label;
        oos = outputStream;
        ois = inputStream;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        String seria = validareSerie.getText();
        int serie;
        try 
        {
            serie = Integer.parseInt(seria);
            if (serie <= 0) throw new IllegalArgumentException("Seria fiecarei cartele trebuie sa fie numar natural nenul!");
            oos.writeObject(new ValidationRequest(serie));
            oos.reset();
            new Thread(() ->
            {
                try 
                {
                    Object obj = ois.readObject();
                    String ans = (String) obj;
                    raspuns.setText(ans);
                } 
                catch (IOException | ClassNotFoundException ex) 
                {
                    System.out.println("Error receiving answer: " + ex.getMessage());
                }
            }
            ).start();
        }
        catch (IllegalArgumentException iaex)
        {
            JOptionPane.showMessageDialog(new JFrame(), iaex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ex) 
        {
            System.out.println("Error writing validation request: " + ex.getMessage());
        }
    }
    
}
