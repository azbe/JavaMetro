
package metrorex.data;

import static metrorex.data.TipCartela.*;

public class CartelaCalatorii extends Cartela
{
    int nrCalatorii;
    
    public CartelaCalatorii(int serie, TipCartela tipCartela, int calatoriiRamase) throws IllegalArgumentException
    {
        setSerie(serie);
        
        if (tipCartela != CALATORII_10 && tipCartela != CALATORII_2)
            throw new IllegalArgumentException();
       
        setTip(tipCartela);
        if (tipCartela == CALATORII_10)
            nrCalatorii = calatoriiRamase;
        else
            nrCalatorii = calatoriiRamase;
    }

    public int getNrCalatorii() 
    {
        return nrCalatorii;
    }

    public void setNrCalatorii(int nrCalatorii) 
    {
        this.nrCalatorii = nrCalatorii;
    }

    @Override
    public boolean validate()
    {
        if (nrCalatorii == 0) 
            return false;
        --nrCalatorii;
        return true;
    }
    
    @Override
    public String toString() 
    {
        return getSerie() + " - " + ((getTip() == CALATORII_10) ? "10" : "2") + " calatorii - " + nrCalatorii + ((nrCalatorii == 1) ? " ramasa" : " ramase"); 
    }
}
