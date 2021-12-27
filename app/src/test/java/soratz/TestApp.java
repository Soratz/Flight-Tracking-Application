package soratz;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.Arrays;

import java.nio.file.Files;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import java.awt.Rectangle;

import org.sikuli.script.*;

class TestApp {

    @Test
    @Order(2)
    void testGUIBasic() {
        deleteFile("capitals.dat");

        ImagePath.setBundlePath(FileSystems.getDefault().getPath("sikuli_images").toString());

        org.sikuli.script.App guiApp = new org.sikuli.script.App("\"C:\\apps\\scoop\\apps\\openjdk11\\current\\bin\\javaw.exe\"");
        guiApp.setUsing("-jar C:\\apps\\git_dir\\Flight-Tracking-Application\\app\\build\\libs\\app.jar");
        guiApp.open(8);

        org.sikuli.script.App.focus("Flight Tracking Application");

        Rectangle rect0 = Screen.getBounds(Screen.getPrimaryId());
        Region r0 = new Region((int)rect0.getX(),(int) rect0.getY(), (int) rect0.getWidth(), (int)rect0.getHeight());
        Region r1 = null;
        r0.exists("capital_name_text.png");
        try {
            r1 = r0.find("capital_name_text.png");
        } catch (FindFailed ff) {
            ff.printStackTrace();
        }
        Region r3 = null;
        if( r1!= null ) {
            Region r2 = r1.below(10);
            r2.click();
            r2.type("Test01AYT");
            r3 = r2.below(40);
            r3.click();
        }
        
        r0.exists("close_button.png");
        try {
            Region r4 = r0.find("close_button.png");
            r4.click();
        } catch (FindFailed ff) {
            ff.printStackTrace();
        }
    } 
    @Test
    @Order(1)
    void testCapitalSerializeDeserialize() {
        deleteFile("capitals.dat");
        soratz.App appUnderTest1 = new App();

        appUnderTest1.getCapitalTextField().setText("TestCity01");
        appUnderTest1.getBtnAddCapital().doClick();

        int changeByteStart = 0;
        
        ArrayList<Byte> changeByteList = new ArrayList<Byte>(Arrays.asList(
            (byte) 22,
            (byte) 11,
            (byte) 13,
            (byte) 41,
            (byte) 15,
            (byte) 33)
            );
        changeBytes("capitals.dat", changeByteStart, changeByteList);

        App appUnderTest2 = new App();

    }

    void deleteFile(String fileName) {
        try {
            Path targetPath = FileSystems.getDefault().getPath(fileName);
            if( Files.exists(targetPath)) { 
                Files.delete(FileSystems.getDefault().getPath(fileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void changeBytes(String fileName, int changeByteStart, ArrayList<Byte> changeByteList) {
        ArrayList<Byte> fileByteList = new ArrayList<Byte>();
        try(
            InputStream fileInputStream = Files.newInputStream(
                    FileSystems.getDefault().getPath(fileName));
        ) {
            int c;
            while( (c=fileInputStream.read()) != -1) {
                fileByteList.add((byte)c);
            }
        } catch( IOException e ) {
            e.printStackTrace();
        }

        for(int i=changeByteStart; i<changeByteStart+changeByteList.size() && i<fileByteList.size(); i++) {
            fileByteList.set(i, changeByteList.get(i-changeByteStart));
        }

        try(
            OutputStream fileOutputStream = Files.newOutputStream(
                    FileSystems.getDefault().getPath(fileName));
        ) {
            for(int i=0; i<fileByteList.size(); i++) {
                fileOutputStream.write(fileByteList.get(i));
            }
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}