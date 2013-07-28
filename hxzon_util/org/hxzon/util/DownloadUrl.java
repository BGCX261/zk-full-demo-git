package org.hxzon.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DownloadUrl {
    public static final int BUFFER_SIZE = 1024;

    public static void saveToFile(String downUrl, String filePath) {

        HttpURLConnection connect = null;
        BufferedInputStream in = null;
        FileOutputStream file = null;
        byte[] buf = new byte[BUFFER_SIZE];
        int size = 0;

        try {
            URL url = new URL(downUrl);
            connect = (HttpURLConnection) url.openConnection();
            connect.connect();
            in = new BufferedInputStream(connect.getInputStream());
            file = FileUtils.openOutputStream(new File(filePath));
            while ((size = in.read(buf)) != -1) {
                file.write(buf, 0, size);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(file);
            IOUtils.closeQuietly(in);
            if (connect != null) {
                connect.disconnect();
            }
        }

    }

//	public void saveToFile(String downUrl, String filePath) {
//
//		HttpURLConnection connect = null;
//		BufferedInputStream in = null;
//		FileOutputStream file = null;
//		byte[] buf = new byte[BUFFER_SIZE];
//		int size = 0;
//
//		try {
//			URL url = new URL(downUrl);
//			connect = (HttpURLConnection) url.openConnection();
//			connect.connect();
//			in = new BufferedInputStream(connect.getInputStream());
//			file = new FileOutputStream(filePath);
//			while ((size = in.read(buf)) != -1) {
//				file.write(buf, 0, size);
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				file.close();
//				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			connect.disconnect();
//		}
//
//	}
}
