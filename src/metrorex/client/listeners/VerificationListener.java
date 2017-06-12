
package metrorex.client.listeners;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import metrorex.data.*;
import metrorex.requests.*;
import metrorex.requests.VerificationRequest.VerificationType;
import static metrorex.requests.VerificationRequest.VerificationType.*;

public class VerificationListener implements ActionListener
{
    JTextField verificareSerie;
    JLabel verificareRaspuns;
    VerificationType type;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    
    public VerificationListener(ObjectOutputStream outputStream, ObjectInputStream inputStream)
    {
        type = ALL;
        oos = outputStream;
        ois = inputStream;
    }
    
    public VerificationListener(JTextField serieField, JLabel raspunsLabel, ObjectOutputStream outputStream, ObjectInputStream inputStream)
    {
        verificareSerie = serieField;
        verificareRaspuns = raspunsLabel;
        type = SPECIFIC;
        oos = outputStream;
        ois = inputStream;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        if (type == SPECIFIC)
        {
            String seria = verificareSerie.getText();
            int serie;
            try 
            {
                serie = Integer.parseInt(seria);
                if (serie <= 0) throw new NumberFormatException();
                oos.writeObject(new VerificationRequest(SPECIFIC, serie));
                oos.reset();
                new Thread(() ->
                {
                    Cartela cartela;
                    try
                    {
                        Object obj = ois.readObject();
                        if (obj instanceof SQLException)
                        {
                            JOptionPane.showMessageDialog(new JFrame(), ((SQLException)obj).getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
                            verificareRaspuns.setText("");
                            return;
                        }
                        else if (obj instanceof Cartela)
                        {
                            cartela = (Cartela) obj;
                            verificareRaspuns.setText(cartela.toString());
                        }
                    }
                    catch (IOException | ClassNotFoundException ioex)
                    {
                        System.out.println("Error reading results: " + ioex.getMessage());
                    }
                }
                ).start();
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(new JFrame(), "Seria fiecarei cartele trebuie sa fie numar natural nenul!", "Input Error", JOptionPane.ERROR_MESSAGE);
            } 
            catch (IOException ex) 
            {
                System.out.println("Error sending verification request: " + ex.getMessage());
            }
        }
        else
        {
            try
            {
                oos.writeObject(new VerificationRequest(ALL, -1));
                oos.reset();
                new Thread(() ->
                {
                    ArrayList<Cartela> cartele = new ArrayList<Cartela>();
                    try
                    {
                        Object obj = ois.readObject();
                        if (obj instanceof SQLException)
                        {
                            JOptionPane.showMessageDialog(new JFrame(), ((SQLException)obj).getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        else
                        {
                            cartele = (ArrayList<Cartela>) obj;
                            
                            JFrame resultsWindow = new JFrame();
                            JPanel results = new JPanel();
                            JScrollPane scrollArea = new JScrollPane(results);
                            scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                            resultsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            resultsWindow.setBounds(150, 100, 300, 600);
                            resultsWindow.getContentPane().add(scrollArea);
                            results.setLayout(new GridBagLayout());
                            GridBagConstraints constr;

                            for (int index = 0; index < cartele.size(); index++)
                            {
                                JLabel text = new JLabel(cartele.get(index).toString());
                                constr = new GridBagConstraints();
                                constr.gridx = 0;
                                constr.gridy = index;
                                constr.weighty = 1;
                                constr.fill = GridBagConstraints.BOTH;
                                constr.insets = new Insets(6, 6, 6, 6);
                                results.add(text, constr);
                            }

                            resultsWindow.setVisible(true);
                        }
                    }
                    catch (IOException | ClassNotFoundException ioex)
                    {
                        System.out.println("Error reading results: " + ioex.getMessage());
                    }
                }
                ).start();
            }
            catch (IOException ioex)
            {
                System.out.println("Error sending verification request: " + ioex.getMessage());
            }
        }
    }
    
}
