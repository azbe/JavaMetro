
package metrorex.requests;

import java.io.Serializable;
import static metrorex.requests.VerificationRequest.VerificationType.*;

public class VerificationRequest implements Serializable
{
    public enum VerificationType { ALL, SPECIFIC; }
    
    public VerificationType tip;
    public int serie;
    
    public VerificationRequest(VerificationType tip, int serie)
    {
        this.tip = tip;
        if (tip == SPECIFIC)
            this.serie = serie;
        else serie = -1;
    }
}
