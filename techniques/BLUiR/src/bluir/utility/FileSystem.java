package bluir.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

public class FileSystem
{
	List<String> files = new java.util.ArrayList();
	int i = 0;
	
	public List<String> getAllJavaFiles(String path, int offset) throws IOException
	{
		File[] faFiles = new File(path).listFiles();
		File[] arrayOfFile1; int k = (arrayOfFile1 = faFiles).length; for (int j = 0; j < k; j++) { File file = arrayOfFile1[j];
			
			if (file.getName().endsWith(".java")) {
				this.files.add(file.getAbsolutePath());
			}
			

			if (file.isDirectory())
			{
				getAllJavaFiles(file.getAbsolutePath(), offset);
			}
		}
		return this.files;
	}
	
	public List<String> getAllZipFiles(String path, int offset) throws IOException
	{
		File[] faFiles = new File(path).listFiles();
		File[] arrayOfFile1; int k = (arrayOfFile1 = faFiles).length; for (int j = 0; j < k; j++) { File file = arrayOfFile1[j];
			
			if (file.getName().endsWith(".zip")) {
				this.files.add(file.getAbsolutePath());
			}
			
			if (file.isDirectory()) {
				getAllZipFiles(file.getAbsolutePath(), offset);
			}
		}
		return this.files;
	}
	
	public List<String> getAllFiles(String path, int offset) throws IOException
	{
		File[] faFiles = new File(path).listFiles();
		File[] arrayOfFile1; int k = (arrayOfFile1 = faFiles).length; for (int j = 0; j < k; j++) { File file = arrayOfFile1[j];
			
			this.files.add(file.getAbsolutePath().substring(offset + 1));
		}
		return this.files;
	}
	
	public static String readFile(String path)
		throws IOException
	{
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0L, fc.size());
			
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}
	}
	
	public static void main(String[] args)
		throws IOException
	{
		FileSystem fs = new FileSystem();
		String path = "/Users/ripon/Downloads/swt-3/src";
		System.out.println(fs.getAllJavaFiles(path, path.length()).size());
	}
}

