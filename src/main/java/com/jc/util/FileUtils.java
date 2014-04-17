package com.jc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils{
	
	private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);
	
	public static void copyFile(File src, File dest) throws IOException {
		if(src.isDirectory()){
			if(!dest.exists())
				dest.mkdir();
			for(String file:src.list()){
				File srcFile = new File(src,file);
				File destFile = new File(dest,file);
				copyFile(srcFile, destFile);
			}	
				
		}else{
			InputStream in = new FileInputStream(src);
	        OutputStream out = new FileOutputStream(dest); 
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = in.read(buffer)) > 0){
	    	   out.write(buffer, 0, length);
	        }
	        in.close();
	        out.close();
		}
	}
	
	public static void zipFile(File src, File zip) throws IOException {
		FileOutputStream fos = new FileOutputStream(zip);
		ZipOutputStream zos = new ZipOutputStream(fos);
		
		String path = src.getAbsolutePath();
		LOG.debug("begin zip direcotry '"+path+"',and zip file locate in '"+zip.getAbsolutePath()+"'");
		int index = path.length()+1;
		
		zipFile(src,zos,index);
		
		zos.closeEntry();
		zos.close();
	}

	public static void zipFile(File src, ZipOutputStream zos,int index)
			throws IOException, FileNotFoundException {
		FileInputStream in = null;

		byte[] buffer = new byte[1024];
		if (src.isDirectory()) {
			for (String file : src.list()) {
				zipFile(new File(src,file),zos,index);
			}
		} else {
			LOG.debug("");
			String path = src.getAbsolutePath();
			LOG.debug("appending file:"+path+", index is"+index);
			ZipEntry ze = new ZipEntry("weiboBackup/"+path.substring(index, path.length()));
			zos.putNextEntry(ze);
			in = new FileInputStream(src);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			in.close();
		}
	}
} 