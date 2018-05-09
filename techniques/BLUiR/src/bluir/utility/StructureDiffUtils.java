package bluir.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.eclipse.core.runtime.CoreException;



public final class StructureDiffUtils
{
	private static final int BUFFER_LENGTH = 1024;
	
	public static String readFile(String filePath)	throws CoreException, IOException
	{
		//char[] buf = new char['�'];
//		InputStreamReader isr = null;
//		isr = new InputStreamReader(new FileInputStream(filePath));
//		StringBuffer sb = new StringBuffer();
//		try {
//			int n;
//			while ((n = isr.read(b)) > 0) { int n;
//				sb.append(b, 0, n);
//			}
//			isr.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		char[] buf = new char[BUFFER_LENGTH];
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath));
		StringBuffer sb = new StringBuffer();
		int n;
		while ( (n=isr.read(buf)) >0 ){
				sb.append(buf, 0, n);
		}
		isr.close();
		return sb.toString();
	}
}

