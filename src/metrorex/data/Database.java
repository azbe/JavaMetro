
package metrorex.data;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Scanner;

import static metrorex.data.TipCartela.*;

public class Database implements Closeable
{
    Connection connection;
    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    final String DB_URL = "jdbc:mysql://localhost:3306/sys?useSSL=false";
    
    public Database() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException
    {
        
        try(FileInputStream fis = new FileInputStream("mysql_userpass.txt");
            Scanner in = new Scanner(fis))
        {
            String user = in.next();
            String pass = in.next();
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, user, pass);
        }
    }
    
    public void add(int serie, TipCartela tip) throws SQLException
    {
        add(serie, serie, tip);
    }
    
    public void add(int seriaX, int seriaY, TipCartela tip) throws SQLException
    {
        if (tip == ABONAMENT_ZI || tip == ABONAMENT_LUNAR)
        {
            StringBuilder insert = new StringBuilder("INSERT INTO abonamente (abon_id, zile, blocat) VALUES ");
            for (int index = seriaX; index <= seriaY; index++)
            {
                insert.append("(");
                insert.append(index);
                insert.append(", ");
                insert.append((tip == ABONAMENT_LUNAR) ? 30 : 1);
                insert.append(", ");
                insert.append(0);
                if (index < seriaY) insert.append("), ");
                else insert.append(");");
            }
            try (PreparedStatement statement = connection.prepareStatement(insert.toString()))
            {
                statement.executeUpdate();
            }
        }
        else if (tip == CALATORII_10 || tip == CALATORII_2)
        {
            StringBuilder insert = new StringBuilder("INSERT INTO cartele_calatorii (cc_id, max_calatorii) VALUES ");
            for (int index = seriaX; index <= seriaY; index++)
            {
                insert.append("(");
                insert.append(index);
                insert.append(", ");
                insert.append((tip == CALATORII_10) ? 10 : 2);
                if (index < seriaY) insert.append("), ");
                else insert.append(");");
            }
            try (PreparedStatement statement = connection.prepareStatement(insert.toString()))
            {
                statement.executeUpdate();
            }
        }
    }
    
    public Cartela get(int serie) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM cartele c LEFT JOIN abonamente a ON c.id = a.abon_id LEFT JOIN cartele_calatorii cc ON c.id = cc.cc_id WHERE id = " + serie);
             ResultSet result = statement.executeQuery();)
        {
            Cartela cartela;
            if (!result.next()) throw new SQLException("Nu exista cartela cautata.");
            int id = result.getInt(2);
            if (result.wasNull())
            {
                id = result.getInt(5);
                if (result.wasNull())
                    throw new SQLException("Nu exista cartela cautata.");
                else
                {
                    cartela = new CartelaCalatorii(id, (result.getInt(6) == 10) ? CALATORII_10 : CALATORII_2, result.getInt(7));
                }
            }
            else
            {
                cartela = new Abonament(id, (result.getInt(3) == 30) ? ABONAMENT_LUNAR : ABONAMENT_ZI, result.getBoolean(4));
            }
            
            return cartela;
        }
    }
    
    public List<Cartela> get() throws SQLException
    {
        List<Cartela> cartele = new ArrayList<Cartela>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM cartele c LEFT JOIN abonamente a ON c.id = a.abon_id LEFT JOIN cartele_calatorii cc ON c.id = cc.cc_id");
             ResultSet result = statement.executeQuery();)
        {
            while (result.next())
            {
                Cartela cartela;
                int id = result.getInt(2);
                if (result.wasNull())
                {
                    id = result.getInt(5);
                    if (result.wasNull())
                        throw new SQLException("Nu exista nicio cartela in baza de date.");
                    else
                    {
                        cartela = new CartelaCalatorii(id, (result.getInt(6) == 10) ? CALATORII_10 : CALATORII_2, result.getInt(7));
                    }
                }
                else
                {
                    cartela = new Abonament(id, (result.getInt(3) == 30) ? ABONAMENT_LUNAR : ABONAMENT_ZI, result.getBoolean(4));
                }
                cartele.add(cartela);
            }
            
            return cartele;
        }
    }
    
    public String validate(int serie) throws SQLException
    {
        try (PreparedStatement exp = connection.prepareStatement("SELECT id FROM cartele_expirate WHERE id = " + serie);
             ResultSet rexp = exp.executeQuery())
        {
            if (!rexp.next())
            {
                try (PreparedStatement verif = connection.prepareStatement("SELECT abon_id, cc_id FROM cartele c LEFT JOIN abonamente a ON c.id = a.abon_id LEFT JOIN cartele_calatorii cc ON c.id = cc.cc_id WHERE id = " + serie);
                     ResultSet rverif = verif.executeQuery())
                {
                    if (!rverif.next()) return "Cartela nu exista!";
                    
                    rverif.getInt(1);
                    if (!rverif.wasNull()) //Cartela e un abonament
                    {
                        try (PreparedStatement blocat = connection.prepareStatement("SELECT blocat FROM abonamente WHERE abon_id = " + serie);
                             ResultSet rblocat = blocat.executeQuery())
                        {
                            if (rblocat.next() && rblocat.getBoolean(1) == true)
                                return "Abonamentul este blocat!";
                        }
                        try (PreparedStatement statement = connection.prepareStatement("UPDATE abonamente SET blocat = True WHERE abon_id = " + serie))
                        {
                            statement.executeUpdate();
                            return "Abonamentul a fost validat!";
                        }
                    }
                    else //Cartela de calatorii
                    {
                        try (PreparedStatement statement = connection.prepareStatement("UPDATE cartele_calatorii SET calatorii = calatorii - 1 WHERE cc_id = " + serie))
                        {
                            statement.executeUpdate();
                            return "Cartela de calatorii a fost validata!";
                        }
                    }
                }
            }
            else return "Cartela a expirat!";
        }
    }

    @Override
    public void close() throws IOException 
    {
        try 
        {
            connection.close();
        }
        catch (NullPointerException | SQLException npex)
        {
            System.out.println("Could not close Database connection: " + npex.getMessage());
        }
    }
   
}
