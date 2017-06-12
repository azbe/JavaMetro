
package metrorex.requests;

import java.io.Serializable;
import metrorex.data.TipCartela;

public class AdditionRequest implements Serializable
{
    public int X;
    public int Y;
    public TipCartela Z;
    
    public AdditionRequest(int X, int Y, TipCartela Z)
    {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }
}
