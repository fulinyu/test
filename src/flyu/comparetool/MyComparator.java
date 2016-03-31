package flyu.comparetool;
import java.io.*;
import java.util.*;

public class MyComparator implements Comparator
{

	@Override
	public int compare(Object p1, Object p2)
	{
		File f1=(File)p1;
		File f2=(File)p2;
		
		if(f1.isDirectory()&&f2.isFile()){
			
			return -1;
		}else if(f1.isFile()&&f2.isDirectory()){
			return 1;
		}
		
		
		
			try{ 
				byte[] buf1=f1.getName().toLowerCase().getBytes("unicode");
				byte[] buf2=f2.getName().toLowerCase().getBytes("unicode");
				
				int size = Math.min(buf1.length, buf2.length); 

				for (int i = 0; i < size; i++) { 

					if (buf1[i] < buf2[i]) 

						return -1; 

					else if (buf1[i] > buf2[i]) 

						return 1; 

				} 

				return buf1.length - buf2.length; 

			}catch(UnsupportedEncodingException ex) { 

				return 0; 

			} 

		} 

	}









