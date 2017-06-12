
package metrorex.requests;

import java.io.Serializable;

public class ValidationRequest implements Serializable
{
    public int serie;
    
    public ValidationRequest(int serie)
    {
        this.serie = serie;
    }
}
