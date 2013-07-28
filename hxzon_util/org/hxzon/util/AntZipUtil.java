//AntZipUtil ; apache ant zip
//Winzip使用ASCII编码而JDK强制UTF8编码导致中文文件名时出错
//使用ant.jar内的org.apache.tools.zip包，包可独立抽取使用。
//ZipOutputStream.setEncoding("GBK");以及new ZipFile(zipFilename, "GBK");
package org.hxzon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.hxzon.org.apache.tools.zip.ZipEntry;
import org.hxzon.org.apache.tools.zip.ZipFile;
import org.hxzon.org.apache.tools.zip.ZipOutputStream;

public class AntZipUtil {
    public final static String encoding = "GBK";

    public static void zip(String outputFileName, String inputDirectory) {
        ZipOutputStream out;
        try {//will create output parent directory
            File file = new File(outputFileName);
            if (file.exists()) {
                if (file.isDirectory()) {
                    throw new IOException("File '" + file + "' exists but is a directory");
                }
                if (file.canWrite() == false) {
                    throw new IOException("File '" + file + "' cannot be written to");
                }
            } else {
                File parent = file.getParentFile();
                if (parent != null && parent.exists() == false) {
                    if (parent.mkdirs() == false) {
                        throw new IOException("File '" + file + "' could not be created");
                    }
                }
            }
            out = new ZipOutputStream(new FileOutputStream((file)));
            out.setEncoding(encoding);
            doZip(out, new File(inputDirectory), "");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void zip(String inputDirectory) {
        zip(inputDirectory + ".zip", inputDirectory);
    }

    private static void doZip(ZipOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                doZip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            // System.out.println(base);
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
        }
    }

    public static void unzip(String inputFileName) {
        int index = inputFileName.lastIndexOf(".");
        String outputDirectory = inputFileName.substring(0, index);
        unzip(inputFileName, outputDirectory);
    }

    public static void unzip(String outputDirectory, String zipFilename) {
        try {
            File outFile = new File(outputDirectory);
            if (!outFile.exists()) {
                outFile.mkdirs();
            }
            ZipFile zipFile = new ZipFile(zipFilename, encoding);
            Enumeration en = zipFile.getEntries();
            ZipEntry zipEntry = null;
            while (en.hasMoreElements()) {
                zipEntry = (ZipEntry) en.nextElement();
                if (zipEntry.isDirectory()) {
                    // mkdir directory
                    String dirName = zipEntry.getName();
                    dirName = dirName.substring(0, dirName.length() - 1);
                    File f = new File(outFile.getPath() + "/" + dirName);
                    f.mkdirs();
                } else {
                    // unzip file
                    File f = new File(outFile.getPath() + "/" + zipEntry.getName());
                    f.createNewFile();
                    InputStream in = zipFile.getInputStream(zipEntry);
                    FileOutputStream out = new FileOutputStream(f);
                    int c;
                    byte[] by = new byte[1024];
                    while ((c = in.read(by)) != -1) {
                        out.write(by, 0, c);
                    }
                    out.close();
                    in.close();
                }
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] temp) {
        AntZipUtil.zip("d:/mkys/byy.zip", "D:/export");// 你要压缩的文件夹
        AntZipUtil.unzip("d:/ddds/byy", "d:/mkys/byy.zip");
    }
}
