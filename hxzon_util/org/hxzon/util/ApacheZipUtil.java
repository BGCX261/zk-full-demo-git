//ApacheZipUtil ; apache.commons.compress.zip
//Winzip use ASCII encoding but JDK force UTF8 encoding make Chinese file name wrong,
//ZipArchiveOutputStream.setEncoding("GBK");以及new ZipFile(zipFilename, "GBK");
//*compress* is a stream based API. It's original code in this component 
//came from Avalon's Excalibur, but originally from Ant, as far as life in Apache goes. 
//It has migrated via: Ant -> Avalon-Excalibur -> Commons-IO -> Commons-Compress.
package org.hxzon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

public class ApacheZipUtil {
    public final static String encoding = "GBK";

    public static void zip(String outputFileName, String inputDirectory) {
        ZipArchiveOutputStream out;
        try {// will create output parent directory
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
            out = new ZipArchiveOutputStream(new FileOutputStream((file)));
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

    private static void doZip(ArchiveOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putArchiveEntry(new ZipArchiveEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                doZip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putArchiveEntry(new ZipArchiveEntry(base));
            IOUtils.copy(new FileInputStream(f), out);
            out.closeArchiveEntry();
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
            Enumeration<?> en = zipFile.getEntries();
            ZipArchiveEntry zipEntry = null;
            while (en.hasMoreElements()) {
                zipEntry = (ZipArchiveEntry) en.nextElement();
                if (zipEntry.isDirectory()) {
                    // mkdir directory
                    String dirName = zipEntry.getName();
                    dirName = dirName.substring(0, dirName.length() - 1);
                    File f = new File(outFile.getPath() + "/" + dirName);
                    f.mkdirs();
                } else {
                    // unzip file
                    File f = new File(outFile.getPath() + "/" + zipEntry.getName());
//					f.createNewFile();
                    InputStream in = zipFile.getInputStream(zipEntry);
                    OutputStream out = new FileOutputStream(f);
                    IOUtils.copy(in, out);
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
        ApacheZipUtil.zip("d:/mkys/byy.zip", "D:/export");// 你要压缩的文件夹
        ApacheZipUtil.unzip("d:/ddds/byy", "d:/mkys/byy.zip");
    }
}
