/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gapps
 */
import com.jmrtd.mavenproject1.InitAuthICAO;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardException;
import net.sf.scuba.smartcards.CardServiceException;


public class Main {
    /**
     * @param args the command line arguments
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.text.ParseException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, ParseException, GeneralSecurityException {
        // TODO code application logic here
        InitAuthICAO tst = new InitAuthICAO();
        try {
            tst.initCard();
        } catch (CardException | CardServiceException | IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
