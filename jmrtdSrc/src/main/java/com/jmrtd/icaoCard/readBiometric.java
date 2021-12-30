/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmrtd.icaoCard;

import java.io.InputStream;
import org.jmrtd.PassportService;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.icao.DG1File;
import java.io.IOException;
import java.util.Arrays;
import net.sf.scuba.smartcards.CardServiceException;

/**
 *
 * @author gapps
 */
public class readBiometric {
            
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
