
package metrorex.data;
import static metrorex.data.TipCartela.*;

public class Abonament extends Cartela
{
    private int nrZile;
    private boolean blocat;

    public Abonament(int serie, TipCartela tipCartela, boolean eblocat) throws IllegalArgumentException
    {
        setSerie(serie);
        
        if (tipCartela != ABONAMENT_LUNAR && tipCartela != ABONAMENT_ZI)
            throw new IllegalArgumentException();
        
        setTip(tipCartela);
        if (tipCartela == ABONAMENT_LUNAR)
            nrZile = 30;
        else
            nrZile = 1;
        
        blocat = eblocat;
    }
    
    public int getNrZile() 
    {
        return nrZile;
    }

    public void setNrZile(int nrZile) 
    {
        this.nrZile = nrZile;
    }
    
    @Override
    public boolean validate()
    {
        if (blocat) return false;
        return (blocat = true);
    }
    
    public void unblock()
    {
        blocat = false;
    }

    @Override
    public String toString()
    {
        return getSerie() + " - Abonament " + ((getTip() == ABONAMENT_LUNAR) ? "lunar" : "de o zi") + (blocat ? " - BLOCAT" : "");
    }
    
}
