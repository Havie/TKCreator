import java.io.IOException;
import java.util.ArrayList;

public class Driver {
	
	private final static String FileDIR="C:\\Users\\sdatz\\Desktop\\script\\campaign\\mod\\";
	
	
	public static void main (String[] args) throws IOException 
	{
		print("----TK Creator------");
		GUI gui=new GUI();

		//InIDETest();
	}
	
	public static void InIDETest() throws IOException
	{
		Parser p= new Parser();
		try {
			p.TraceProgram(FileDIR, "mod");
		} catch (IOException e) {
			print(" some kind of error in file path");
		}
		
		//p.FindReferences();
		//p.PrintAllFilesFound();
		
		//using this for the oddly specific governors file in which i have a ton of event keys 
		// that need to be cloned for each region
		p.FindSpecificKeys(FileDIR+"@sat_havie_governors.lua", FileDIR+"regions.txt");

	
		ArrayList<String> eventKeys = new ArrayList<String>();
		eventKeys.add("3k_lua_yuanshe_helps_lubi_dilemma");
		p.OutputClonedEventLines(FileDIR+"dilemma_payload.txt", eventKeys);
		p.OutputClonedEventLines(FileDIR+"dilemma_options.txt", eventKeys);
		
		//p.OutputDilemmaTXT("3k_lua_yuanshe_helps_lubi_dilemma",3);
	}





	public static void print(String s){System.out.println(s);}
}
