package com.jc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class DataUtil{

	public static String readFlatFile(File file,String charset) throws FileNotFoundException,IOException{
		FileInputStream fis = new FileInputStream(file);
		byte[] bytebuffer = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		while((len = fis.read(bytebuffer))!=-1){
			baos.write(bytebuffer,0,len);
		}
		fis.close();
		baos.close();
		ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
		String result = Charset.forName(charset).decode(bb).toString();
		return result;
	}
	
	public static String readFlatFile(File file) throws FileNotFoundException,IOException{
		return readFlatFile(file,"UTF-8");
	}
	
	public static void  writeFlatFile(File file,String content) throws FileNotFoundException,IOException{
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes());
		fos.close();
	}

}
