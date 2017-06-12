package metrorex.client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import metrorex.client.listeners.*;

public class Client
{
    private final Socket client;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    
    private final JFrame mainWindow;
    private final JPanel[] mainPanels;
    
    public Client(String ip, int port) throws IOException
    {
        client = new Socket(ip, port);
        outputStream = new ObjectOutputStream(client.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(client.getInputStream());
        
        mainWindow = new JFrame("Metrorex");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) 
            {
                try 
                {
                    outputStream.writeObject(new String());
                    inputStream.close();
                    outputStream.close();
                    client.close();
                } 
                catch (IOException ex) 
                {
                    System.out.println("Error closing socket/streams: " + ex.getMessage());
                }
            }
        });
        mainWindow.setBounds(100, 100, 550, 250);
        mainWindow.setLayout(new GridBagLayout());
        mainWindow.setMinimumSize(new Dimension(450, 250));
        GridBagConstraints constr;
        
        mainPanels = new JPanel[5];
        for (int index = 0; index < 5; index++)
        {
            mainPanels[index] = new JPanel();
            constr = new GridBagConstraints();
            constr.gridx = 0;
            constr.gridy = index;
            constr.fill = GridBagConstraints.BOTH;
            constr.weightx = 1;
            constr.weighty = 1;
            constr.insets = new Insets(6, 6, 6, 6);
            mainPanels[index].setLayout(new GridBagLayout());
            mainWindow.add(mainPanels[index], constr);
        }
        
        createPanel0();
        createPanel1();
        createPanel2();
        createPanel3();
        createPanel4();
        
        mainWindow.setVisible(true);
    }
    
    private void createPanel0()
    {
        GridBagConstraints constr;
        
        JButton adaugareButon = new JButton("Adaugare");
        constr = new GridBagConstraints();
        constr.gridx = 0;
        constr.gridy = 0;
        mainPanels[0].add(adaugareButon, constr);
        
        JLabel adaugareSerii = new JLabel(" Seriile ");
        constr = new GridBagConstraints();
        constr.gridx = 1;
        constr.gridy = 0;
        mainPanels[0].add(adaugareSerii, constr);
        
        JTextField adaugareSeriaX = new JTextField();
        adaugareSeriaX.setMargin(new Insets(2,2,2,2));
        adaugareSeriaX.setHorizontalAlignment(JTextField.CENTER);
        constr = new GridBagConstraints();
        constr.gridx = 2;
        constr.gridy = 0;
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.weightx = 1;
        mainPanels[0].add(adaugareSeriaX, constr);
        
        JLabel adaugareMinus = new JLabel(" - ");
        constr = new GridBagConstraints();
        constr.gridx = 3;
        constr.gridy = 0;
        mainPanels[0].add(adaugareMinus, constr);
        
        JTextField adaugareSeriaY = new JTextField();
        adaugareSeriaY.setMargin(new Insets(2,2,2,2));
        adaugareSeriaY.setHorizontalAlignment(JTextField.CENTER);
        constr = new GridBagConstraints();
        constr.gridx = 4;
        constr.gridy = 0;
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.weightx = 1;
        mainPanels[0].add(adaugareSeriaY, constr);
        
        JLabel adaugareTipLabel = new JLabel(" Tip ");
        constr = new GridBagConstraints();
        constr.gridx = 5;
        constr.gridy = 0;
        mainPanels[0].add(adaugareTipLabel, constr);
        
        JTextField adaugareTip = new JTextField();
        adaugareTip.setToolTipText("Optiuni valide (non case-sensitive): 'Abonament Lunar', 'Abonament Zi', '10 Calatorii', '2 Calatorii'");
        adaugareTip.setMargin(new Insets(2,2,2,2));
        adaugareTip.setHorizontalAlignment(JTextField.CENTER);
        constr = new GridBagConstraints();
        constr.gridx = 6;
        constr.gridy = 0;
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.weightx = 2;
        mainPanels[0].add(adaugareTip, constr);
        
        adaugareButon.addActionListener(new AdditionListener(adaugareSeriaX, adaugareSeriaY, adaugareTip, outputStream, inputStream));    
    }
    
    private void createPanel1()
    {
        GridBagConstraints constr;
        
        JButton adaugareButon = new JButton("Adaugare");
        constr = new GridBagConstraints();
        constr.gridx = 0;
        constr.gridy = 0;
        mainPanels[1].add(adaugareButon, constr);
        
        JLabel adaugareSeria = new JLabel(" Seria ");
        constr = new GridBagConstraints();
        constr.gridx = 1;
        constr.gridy = 0;
        mainPanels[1].add(adaugareSeria, constr);
        
        JTextField adaugareSerie = new JTextField();
        adaugareSerie.setMargin(new Insets(2,2,2,2));
        adaugareSerie.setHorizontalAlignment(JTextField.CENTER);
        constr = new GridBagConstraints();
        constr.gridx = 2;
        constr.gridy = 0;
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.weightx = 1;
        mainPanels[1].add(adaugareSerie, constr);
        
        JLabel adaugareTipLabel = new JLabel(" Tip ");
        constr = new GridBagConstraints();
        constr.gridx = 3;
        constr.gridy = 0;
        mainPanels[1].add(adaugareTipLabel, constr);
        
        JTextField adaugareTip = new JTextField();
        adaugareTip.setToolTipText("Optiuni valide (non case-sensitive): 'Abonament Lunar', 'Abonament Zi', '10 Calatorii', '2 Calatorii'");
        adaugareTip.setMargin(new Insets(2,2,2,2));
        adaugareTip.setHorizontalAlignment(JTextField.CENTER);
        constr = new GridBagConstraints();
        constr.gridx = 4;
        constr.gridy = 0;
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.weightx = 2;
        mainPanels[1].add(adaugareTip, constr);
        
        adaugareButon.addActionListener(new AdditionListener(adaugareSerie, adaugareSerie, adaugareTip, outputStream, inputStream));
    }
    
    private void createPanel2()
    {
        GridBagConstraints constr;
        
        JButton validareButon = new JButton("Validare");
        constr = new GridBagConstraints();
        constr.gridx = 0;
        constr.gridy = 0;
        mainPanels[2].add(validareButon, constr);
        
        JLabel validareSeria = new JLabel(" Seria ");
        constr = new GridBagConstraints();
        constr.gridx = 1;
        constr.gridy = 0;
        mainPanels[2].add(validareSeria, constr);
        
        JTextField validareSerie = new JTextField();
        validareSerie.setMargin(new Insets(2,2,2,2));
        validareSerie.setHorizontalAlignment(JTextField.CENTER);
        constr = new GridBagConstraints();
        constr.gridx = 2;
        constr.gridy = 0;
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.weightx = 1;
        mainPanels[2].add(validareSerie, constr);
        
        JLabel validareRaspuns = new JLabel();
        constr = new GridBagConstraints();
        constr.gridx = 3;
        constr.gridy = 0;
        constr.weightx = 1;
        mainPanels[2].add(validareRaspuns, constr);
        
        validareButon.addActionListener(new ValidationListener(validareSerie, validareRaspuns, outputStream, inputStream));
    }
    
    private void createPanel3()
    {
        GridBagConstraints constr;
        
        JButton verificareButon = new JButton("Verificare");
        constr = new GridBagConstraints();
        constr.gridx = 0;
        constr.gridy = 0;
        mainPanels[3].add(verificareButon, constr);
        
        JLabel verificareSeria = new JLabel(" Seria ");
        constr = new GridBagConstraints();
        constr.gridx = 1;
        constr.gridy = 0;
        mainPanels[3].add(verificareSeria, constr);
        
        JTextField verificareSerie = new JTextField();
        verificareSerie.setMargin(new Insets(2,2,2,2));
        verificareSerie.setHorizontalAlignment(JTextField.CENTER);
        constr = new GridBagConstraints();
        constr.gridx = 2;
        constr.gridy = 0;
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.weightx = 1;
        mainPanels[3].add(verificareSerie, constr);
        
        JLabel verificareRaspuns = new JLabel();
        constr = new GridBagConstraints();
        constr.gridx = 3;
        constr.gridy = 0;
        constr.weightx = 1;
        mainPanels[3].add(verificareRaspuns, constr);
        
        verificareButon.addActionListener(new VerificationListener(verificareSerie, verificareRaspuns, outputStream, inputStream));
    }
    
    private void createPanel4()
    {
        GridBagConstraints constr;
        
        JButton verificareButon = new JButton("Afisare");
        constr = new GridBagConstraints();
        constr.gridx = 0;
        constr.gridy = 0;
        mainPanels[4].add(verificareButon, constr);
        
        verificareButon.addActionListener(new VerificationListener(outputStream, inputStream));
    }
    
}
