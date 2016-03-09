package com.applozic.mobicomkit.contact;

/**
 * Created by devashish on 08/03/16.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


/**
 * Parser code for parsing the vCard file and creating a CSV file.
 */
public class MobiComVCFParser
{

    public static final String BEGIN_VCARD = "BEGIN:VCARD";
    public static final String END_VCARD = "END:VCARD";
    public static final String VERSION = "VERSION:2.1";

    public VCFContactData vcfContactData;

    public VCFContactData parseCVFContactData(String filePath) throws  Exception {

        File file = new File(filePath);
        FileReader fin = new FileReader(file);
        BufferedReader in = new BufferedReader(fin);
        StringBuffer contactBuffer = new StringBuffer();
        StringBuffer imageByteCode = null;
        for (String sLine = in.readLine(); sLine != null; sLine = in.readLine()) {
            if (sLine.equalsIgnoreCase(BEGIN_VCARD)) {

                //START
                vcfContactData = new VCFContactData();

            } else if (sLine.equalsIgnoreCase(END_VCARD)) {

                //END
                vcfContactData.setTelephoneNumber(contactBuffer.toString());
                if(imageByteCode!=null){
                    vcfContactData.setProfilePic(stringToBitMap(imageByteCode.toString()));
                }
                return vcfContactData;

            } else if (sLine.startsWith("FN:")) {

                String[] tokens = sLine.split(":");
                if (tokens.length == 2) {
                    vcfContactData.setName(tokens[1]);
                }

            } else if (sLine.startsWith("TEL;")) {
                String[] tokens = sLine.split(":");
                if (tokens.length == 2) {
                    contactBuffer.append(tokens[1] + "\n");
                }
            } else if (sLine.startsWith("PHOTO")) {

                String[] tokens = sLine.split(":");
                if (tokens.length >= 2) {
                    imageByteCode = new StringBuffer().append(tokens[1]);
                }
            }else {
                if(imageByteCode!=null){
                    imageByteCode.append(sLine);
                }
            }
        }
        return null;
    }
    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            Log.e("MobiComVParser", encodedString , e);
            return null;
        }
    }

}

