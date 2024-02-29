package io.github.cctyl;

import io.github.cctyl.domain.dto.Info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

    }

    public void decrypt(String fileName) {

        final byte key = 10;
        final String unDecryptSuffix = ".dat";
        final byte[] buffer = new byte[2048];

        String fileSuffix = null;
        String substring = null;
        FileOutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        try {
            File file = new File(fileName);
            int i = file.getName().lastIndexOf(".dat");
            if (i == -1) {

                System.out.println("解码失败");
                return;
            }

            fileInputStream = new FileInputStream(file);

            byte[] buf = new byte[8];

            int len = fileInputStream.read(buffer);
            for (int j = 0; j < buf.length; j++) {
                buf[j] = buffer[j];
            }

            fileSuffix = getFileSuffix(key, buf);

            substring = file.getName().substring(0, i);
            if (unDecryptSuffix.equals(fileSuffix)) {
                System.out.println(file.getName() + "解码失败");
                return;
            }


            outputStream = new FileOutputStream(new File(file.getParentFile().getAbsolutePath(), +File.pathSeparatorChar + substring + fileSuffix));
            outputStream.write(buffer, 0, len);
            while ((len = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            System.out.println(substring + fileSuffix + "解码成功");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    public Info getKey(byte[] buf) {
        Info info = new Info();
        info.setKey((byte) -1);
        for (int key = 0x01; key < 0xFF; key++) {
            byte key1 = (byte) key;
            byte[] ints = convertData(buf, key1);
            String fileSuffix = getFileSuffix(ints);
            if (
                    !".dat".equals(fileSuffix)
            ) {
                info.setSuffix(fileSuffix);
                info.setKey(key1);
                break;
            }
        }
        return info;
    }

    public String getFileSuffix(byte key, byte[] buf) {
        byte[] ints = convertData(buf, key);
        return getFileSuffix(ints);
    }

    private byte[] convertData(byte[] buf, byte key) {

        byte[] result = new byte[buf.length];
        for (int i = 0; i < buf.length; i++) {
            result[i] = (byte) (buf[i] ^ key);
        }
        return result;

    }


    public String getFileSuffix(byte[] data) {


        switch (data[0]) {
            case (byte) 0XFF:  //byte[] jpg = new byte[] { 0xFF, 0xD8, 0xFF };
            {
                if (data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF) {
                    return ".jpg";
                }
                break;
            }
            case (byte) 0x89:  //byte[] png = new byte[] { 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
            {
                if (data[1] == (byte) 0x50 && data[2] == (byte) 0x4E && data[7] == (byte) 0x0A) {
                    return ".png";
                }
                break;
            }
            case (byte) 0x42:  //byte[] bmp = new byte[] { 0x42, 0x4D };
            {
                if (data[1] == (byte) 0X4D) {
                    return ".bmp";
                }
                break;
            }
            case (byte) 0x47:  //byte[] gif = new byte[] { 0x47, 0x49, 0x46, 0x38, 0x39(0x37), 0x61 };
            {
                if (data[1] == (byte) 0x49 && data[2] == (byte) 0x46 && data[3] == (byte) 0x38 && data[5] == (byte) 0x61) {
                    return ".gif";
                }
                break;
            }
            case (byte) 0x49:  // byte[] tif = new byte[] { 0x49, 0x49, 0x2A, 0x00 };
            {
                if (data[1] == (byte) 0x49 && data[2] == (byte) 0x2A && data[3] == (byte) 0x00) {
                    return ".tif";
                }
                break;
            }
            case (byte) 0x4D:  //byte[] tif = new byte[] { 0x4D, 0x4D, 0x2A, 0x00 };
            {
                if (data[1] == (byte) 0x4D && data[2] == (byte) 0x2A && data[3] == (byte) 0x00) {
                    return ".tif";
                }
                break;
            }
        }

        return ".dat";
    }
}
