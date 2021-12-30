/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmrtd.mavenproject1;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;
import org.jmrtd.BACKey;
import org.jmrtd.PACEKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardAccessFile;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.PACEInfo;
import org.jmrtd.lds.SecurityInfo;
import org.jmrtd.lds.icao.DG1File;
/**
 * s
 * @author gapps
 */
public class Test {
    
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
    
    
    public void initCard() throws CardException, CardServiceException, IOException, NoSuchAlgorithmException, ParseException, GeneralSecurityException{

        attempFunction(1);
       
    }
    
    
    public Boolean attempFunction(int wReader) throws CardException, CardServiceException, IOException, NoSuchAlgorithmException, ParseException{
        TerminalFactory factory = TerminalFactory.getInstance("PC/SC", null);
        CardTerminal terminal = factory.terminals().list().get(wReader);
        CardService cs = CardService.getInstance(terminal);
        PassportService ps = new PassportService(cs, 256, 224, false, false);
        try {
            ps.open();    
            List <CardTerminal> terminals;
            terminals = TerminalFactory.getDefault().terminals().list();
            System.out.println(terminals + "\n");

            byte[] Atr = ps.getATR();
            StringBuilder builder = new StringBuilder();
            for(byte b : Atr) {
                builder.append(String.format("%02x", b));
            }
            String AtrHex = builder.toString().toUpperCase();
            
            System.out.println("ATR= " + AtrHex);
            BACKey backey = BACKeyGenerate("000000000000000","1996-11-10", "2020-11-23"); //DocNumber, BirthDate, Expdate
            
            //PACE Authentication
           
            String autRes = PACEAuthRes("530163",PassportService.EF_CARD_ACCESS, ps);
           
            System.out.println(autRes);
            
            //BAC Authentication
            //ps.doBAC(backey); -----------------------------------BAC Auth
            
            ps.sendSelectApplet(true);
            ps.getInputStream(PassportService.EF_COM).read();
            InputStream is1;
            is1 = ps.getInputStream(PassportService.EF_DG1);

            // Lee los datos basicos de la tarjeta
            DG1File dg1 = (DG1File) LDSFileUtil.getLDSFile(PassportService.EF_DG1, is1);
            System.out.println("------------DG1----------");
            System.out.println("DocumentNumber: " + dg1.getMRZInfo().getDocumentNumber());
            System.out.println("Gender: " + dg1.getMRZInfo().getGender());
            System.out.println("DateOfBirth: " + dg1.getMRZInfo().getDateOfBirth());
            System.out.println("DateOfExpiry: " + dg1.getMRZInfo().getDateOfExpiry());
            System.out.println("DocumentCode: " + dg1.getMRZInfo().getDocumentCode());
            System.out.println("IssuingState: " + dg1.getMRZInfo().getIssuingState());
            System.out.println("Nationality: " + dg1.getMRZInfo().getNationality());
            System.out.println("OptionalData1: " + dg1.getMRZInfo().getOptionalData1());
            System.out.println("OptionalData2: " + dg1.getMRZInfo().getOptionalData2());
            System.out.println("PersonalNumber: " + dg1.getMRZInfo().getPersonalNumber());
            System.out.println("PrimaryIdentifier: " + dg1.getMRZInfo().getPrimaryIdentifier());
            System.out.println("SecondaryIdentifier: " + dg1.getMRZInfo().getSecondaryIdentifier());

            is1.close();
            return true;

        } catch (CardServiceException | IOException  e) {
            System.out.println("errors"); 
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            ps.close();
            return false;
/*        } catch (ParseException e) {
            System.out.println(e.getMessage());        
          }*/
        }}
}
