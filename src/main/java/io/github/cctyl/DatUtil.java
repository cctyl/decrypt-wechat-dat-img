package io.github.cctyl;

import cn.hutool.core.io.FileUtil;
import io.github.cctyl.domain.dto.Info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatUtil {


    public static StopbilityThread<File> start(String path) {


        List<File> files = scanFiles(path);
        File file = files.get(0);
        final byte key = getKey(file);

        LogTool.log("获得key：" + Integer.toBinaryString(key));

        return new StopbilityThread<>(files, f -> {
            decrypt(f, key);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, (i) -> {
            LogTool.log("执行完毕,共解码"+i+"条数据");
        }
        );

    }

    private static byte getKey(File file) {

        try (FileInputStream fileInputStream = new FileInputStream(file);) {
            byte[] buf = new byte[8];
            fileInputStream.read(buf);

            byte key = getKey(buf).getKey();
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<File> scanFiles(String path) {
        return FileUtil.loopFiles(path, pathname -> pathname.getName().endsWith(".dat"));
    }

    private static void decrypt(File sourceFile, final byte key) {

        final String unDecryptSuffix = ".dat";
        final byte[] buffer = new byte[2048];

        String fileSuffix = null;
        String substring = null;
        FileOutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        try {
            int i = sourceFile.getName().lastIndexOf(".dat");
            if (i == -1) {

                LogTool.log("解码失败");
                return;
            }

            fileInputStream = new FileInputStream(sourceFile);

            byte[] buf = new byte[8];

            int len = fileInputStream.read(buffer);
            for (int j = 0; j < buf.length; j++) {
                buf[j] = buffer[j];
            }

            fileSuffix = getFileSuffix(key, buf);

            substring = sourceFile.getName().substring(0, i);
            if (unDecryptSuffix.equals(fileSuffix)) {
                LogTool.log(sourceFile.getName() + "解码失败");
                return;
            }

            outputStream = new FileOutputStream(new File(sourceFile.getParentFile().getAbsolutePath(), +File.pathSeparatorChar + substring + fileSuffix));
            decrypt(buffer, len, key);
            outputStream.write(buffer, 0, len);
            while ((len = fileInputStream.read(buffer)) != -1) {
                decrypt(buffer, len, key);
                outputStream.write(buffer, 0, len);
            }
            LogTool.log(substring + fileSuffix + "解码成功");
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

    private static void decrypt(byte[] buffer, int len, byte key) {

        for (int i = 0; i < len; i++) {
            buffer[i] = (byte) (buffer[i] ^ key);
        }
    }

    private static Info getKey(byte[] buf) {
        Info info = new Info();
        info.setKey((byte) -1);
        for (int key = 0x01; key < 0xFF; key++) {
            byte key1 = (byte) key;
            byte[] ints = convertData(buf, key1);
            String fileSuffix = getFileSuffix(ints);
            if (!".dat".equals(fileSuffix)) {
                info.setSuffix(fileSuffix);
                info.setKey(key1);
                break;
            }
        }
        return info;
    }

    private static String getFileSuffix(byte key, byte[] buf) {
        byte[] ints = convertData(buf, key);
        return getFileSuffix(ints);
    }

    private static byte[] convertData(byte[] buf, byte key) {

        byte[] result = new byte[buf.length];
        for (int i = 0; i < buf.length; i++) {
            result[i] = (byte) (buf[i] ^ key);
        }
        return result;

    }


    private static String getFileSuffix(byte[] data) {


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
