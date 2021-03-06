/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gapps
 */
import com.jmrtd.icaoCard.InitAuthICAO;
import com.jmrtd.icaoCard.readBiometric;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import javax.smartcardio.CardException;
import net.sf.scuba.smartcards.CardServiceException;
import org.jmrtd.PassportService;


public class Main {
    /**
     * @param args the command line arguments
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.text.ParseException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, ParseException, GeneralSecurityException {
        // TODO code application logic here
        InitAuthICAO initialize = new InitAuthICAO();
        readBiometric data = new readBiometric();
        
        try {
            
            PassportService ps = initialize.initCard(1); //The number indicated in initCard is to select the PS/SC Reader number
            data.ReadMRZData(ps);//ps is the service to be able to use passport filesystem without APDUs
            data.extractPhoto(ps);
            
            
        } catch (CardException | CardServiceException | IOException e) {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }
    
}
