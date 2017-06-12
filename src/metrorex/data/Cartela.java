
package metrorex.data;

import java.io.Serializable;
import static metrorex.data.TipCartela.*;

public abstract class Cartela implements Serializable
{
    private int serie;
    private TipCartela tip;
    
    public void Cartela(int serie)
    {
        if (serie < 0) throw new IllegalArgumentException();
        this.serie = serie;
        this.tip = GENERICA;
    }
    
    public int getSerie()
    {
        return serie;
    }
    
    public void setSerie(int serie)
    {
        if (serie < 0) throw new IllegalArgumentException();
        this.serie = serie;
    }

    public TipCartela getTip() 
    {
        return tip;
    }

    public void setTip(TipCartela tip) 
    {
        this.tip = tip;
    }
    
    public abstract boolean validate();
    
    @Override
    public String toString()
    {
        return serie + " - Cartela generica";
    }
}
