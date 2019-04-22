import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader {

	private static BufferedReader br;

	FileReader(String fileName){
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	String getNextValue() throws IOException{
		String temp="";
		if((temp = br.readLine()) != null) {
			return temp.trim();
		}
		return null;
	}

}
