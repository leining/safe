package com.my.safe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtiles {
	public static String streamToString(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while(-1 != (len = in.read(buffer))) {
			out.write(buffer, 0, len);
		}
		String result = out.toString();
		out.close();
		in.close();
		return result;
	}
}
