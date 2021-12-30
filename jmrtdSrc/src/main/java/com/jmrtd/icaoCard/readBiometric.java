/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmrtd.icaoCard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.jmrtd.PassportService;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.icao.DG1File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import net.sf.scuba.smartcards.CardServiceException;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;

/**
 *
 * @author gapps
 */
public class readBiometric {
    
    public void extractPhoto(PassportService ps) throws FileNotFoundException, IOException, CardServiceException{
        
            InputStream is2;
            is2 = ps.getInputStream(PassportService.EF_DG2);
            
            //StandardBiometricHeader(is2).get// sbh = new StandardBiometricHeader();
            DG2File dg2 = new DG2File(is2);
            FaceInfo facedata = dg2.getFaceInfos().get(0);
            FaceImageInfo imgfn = facedata.getFaceImageInfos().get(0);
     
            //StandardBiometricHeader sbh = newFace.getSourceType();
            System.out.println(imgfn);
            
            //FaceInfo imgfn = facedata;
            OutputStream ots = new FileOutputStream("C:\\Users\\win 10\\Documents\\Javacard\\jmrt\\"+"img");
            imgfn.writeObject(ots);
            InputStream its = new FileInputStream("C:\\Users\\win 10\\Documents\\Javacard\\jmrt\\"+"img");
            
            FaceImageInfoLocal imglc = new FaceImageInfoLocal(its) ;

            OutputStream ots2=new FileOutputStream("C:\\Users\\win 10\\Documents\\Javacard\\jmrt\\"+"img"+".jp2");

            imglc.writeImage(ots2);

            its.close();
            ots.close();
            ots2.close();
            is2.close();
    }   
    public void ReadMRZData(PassportService ps) throws CardServiceException, IOException{
        try {
            ps.sendSelectApplet(true); //selection of basic applet for eMRTD documents
            ps.getInputStream(PassportService.EF_COM).read();
            InputStream is1;
            
            is1 = ps.getInputStream(PassportService.EF_DG1);
            

            // Basic data from DG1 read
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
            

        } catch(CardServiceException | IOException e ){
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            ps.close();
        }
    }
}
