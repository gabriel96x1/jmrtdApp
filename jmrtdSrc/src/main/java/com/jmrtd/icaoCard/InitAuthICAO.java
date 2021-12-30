/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmrtd.icaoCard;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;
import org.jmrtd.BACKey;
import org.jmrtd.PACEKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardAccessFile;
import org.jmrtd.lds.PACEInfo;
import org.jmrtd.lds.SecurityInfo;
/**
 * s
 * @author gapps
 */
public class InitAuthICAO {
    
    public static List<PACEInfo> getPACEInfos(Collection<SecurityInfo> securityInfos) {
        List<PACEInfo> paceInfos = new ArrayList<>();

        if (securityInfos == null) {
            return paceInfos;
        }

        for (SecurityInfo securityInfo: securityInfos) {
            //System.out.println(securityInfo);
            if (securityInfo instanceof PACEInfo) {
                paceInfos.add((PACEInfo)securityInfo);
            }
        }

        return paceInfos;
    }
    
    public BACKey BACKeyGenerate(String passNumber, String birth, String expire) throws CardServiceException, ParseException{      
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDate = format.parse(birth);
            Date expireDate = format.parse(expire);
            
            BACKey backey = new BACKey(passNumber, birthDate, expireDate);
            
            return backey;
            
    }
    
    public String PACEAuthRes(String CAN, short nameFile, PassportService ps) throws CardServiceException, IOException{
            
        CardAccessFile cardAccessFile = new CardAccessFile(ps.getInputStream(nameFile, PassportService.DEFAULT_MAX_BLOCKSIZE));
            
            Collection<SecurityInfo> securityInfos = cardAccessFile.getSecurityInfos();
            getPACEInfos(securityInfos);
            ///SecurityInfo securityInfo = securityInfos.iterator().next();
            //System.out.println("ProtocolOIDString: " + securityInfo.getProtocolOIDString());
            //System.out.println("ObjectIdentifier: " + securityInfo.getObjectIdentifier());

           List<PACEInfo> paceInfos = getPACEInfos(securityInfos);
           System.out.println("DEBUG: found a card access file: paceInfos (" + (paceInfos == null ? 0 : paceInfos.size()) + ") = " + paceInfos);

           if (paceInfos != null && paceInfos.size() > 0) { 
                PACEInfo paceInfo = paceInfos.get(0);
                PACEKeySpec paceKey = PACEKeySpec.createCANKey("530163"); // Card Access Number 
                ps.doPACE(paceKey, paceInfo.getObjectIdentifier(), PACEInfo.toParameterSpec(paceInfo.getParameterId()));
                //System.out.println("Unsuccessfully");
                ps.sendSelectApplet(true);
                ps.getInputStream(PassportService.EF_COM).read();
                String res = "PACE Authentication with CAN " + CAN + " Successfully";
                return res;
            } else {
                //System.out.println("Unsuccessfully");
                ps.close();
                return "Unsuccessfully";
            }
             
             
    }
    
    
    public PassportService initCard(int wReader) throws CardException, CardServiceException, IOException, NoSuchAlgorithmException, ParseException{
        
        //PS/SC reader selection and conection
        TerminalFactory factory = TerminalFactory.getInstance("PC/SC", null);
        CardTerminal terminal = factory.terminals().list().get(wReader);
        
        //Service for selected terminal creation
        CardService cs = CardService.getInstance(terminal);
        
        
        PassportService ps = new PassportService(cs, 256, 224, false, false);//Service for an easier way to use Authentications and file reading
        
        try {
            ps.open();    
            
            List <CardTerminal> terminals;
            terminals = TerminalFactory.getDefault().terminals().list();
            System.out.println(terminals + "\n");
            
            //ATR convertion from byte[] to String
            byte[] Atr = ps.getATR();
            StringBuilder builder = new StringBuilder();
            for(byte b : Atr) {
                builder.append(String.format("%02x", b));
            }
            String AtrHex = builder.toString().toUpperCase();
            System.out.println("ATR= " + AtrHex);
            
            //BACKey generation
            BACKey backey = BACKeyGenerate("000000000000000","1996-11-10", "2020-11-23"); //DocNumber, BirthDate, Expdate
            
            //PACE Authentication
            String autRes = PACEAuthRes("530163",PassportService.EF_CARD_ACCESS, ps);
            
            System.out.println(autRes);           
            //BAC Authentication
            //ps.doBAC(backey); -----------------------------------BAC Auth
           
            return ps;

        } catch (CardServiceException | IOException  e) {
            System.out.println("errors"); 
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            ps.close();
            return null;
        }
    }
}
