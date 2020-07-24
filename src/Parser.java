import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;



public class Parser {
	private HashMap<String,Node> map;
	private HashMap<String,Node> newPrograms;
	//private ArrayList<String> folders=new ArrayList<String>();
	//Each Folder name will be the key, the value will be a list of its sub files
	private HashMap<String,ArrayList<String>> filesInFolders= new HashMap<String,ArrayList<String>>();
	private HashMap<String,ArrayList<String>> foldersInFolders = new HashMap<String,ArrayList<String>>();	
	private HashMap<String,String> pathsToFiles = new HashMap<String,String>();
	private String defaultPath="C:\\Users\\";
	private String outputPath="C:\\Users\\TKout.txt";
	private String programName="ignore";

	public enum eTargetType {Target_Region_1, Target_Region_2, Target_Character_1, Target_Character_2, Target_Faction_1,Target_Faction_2};

	public Parser()
	{
		map=new HashMap<String,Node>();
		newPrograms=new HashMap<String,Node>();
	}

	public void TraceProgram(String path, String lastFolder) throws IOException
	{
		if(programName==null) {Driver.print("null"); return;}
		if(programName.equals("")){Driver.print("found empty string in programName"); return;}

		ArrayList<String> subFolders=ParseLocation(path, lastFolder);

		for (String folder : subFolders)
		{	
			TraceProgram(path+"/"+folder, folder); 
		}

	}
	//Looks at the contents of the folder were in, returns a list subfolders to further explore
	private ArrayList<String> ParseLocation(String path, String lastFolder) throws IOException
	{
		ArrayList<String> _folders= new ArrayList<String>();
		File file= new File(path);
		for (File entry : file.listFiles())
		{
			if(entry.isDirectory())
			{
				AddFolder(lastFolder,entry , _folders, foldersInFolders, path );
			}
			else 
			{
				AddFile( lastFolder,  entry,  filesInFolders, path);

			}
		}
		return _folders;
	}

	private void AddFolder(String currentDir, File folder ,ArrayList<String> folders, HashMap<String,ArrayList<String>> foldersInFolders, String path )
	{
		folders.add(folder.getName()); // collection of subfolders we return
		//Lets not keep folders, we dont need
		//if(!pathsTo.containsKey(folder.getName()))
		//	pathsTo.put(folder.getName(), path); // lets keep track of how to get back here, might be faster

		if(foldersInFolders.containsKey(folder)) //just append to the arrayList for that Folder
		{
			ArrayList<String> list= foldersInFolders.get(folder.getName());
			list.add(folder.getName());
		}
		else //First time seeing-Start a new List
		{
			ArrayList<String> list= new ArrayList<String>();
			list.add(folder.getName());
			foldersInFolders.put(currentDir, list);
		}
	}
	private void AddFile(String currDir, File file, HashMap<String,ArrayList<String>> filesInFolders, String path) throws IOException
	{
		//Driver.print("Adding a File:"+ file.getName() +"   To CurrDir: " + currDir);
		if(!pathsToFiles.containsKey(file.getName()))
			pathsToFiles.put(file.getName(), path); // lets keep track of how to get back here

		if( filesInFolders.containsKey(currDir))
		{
			//Folder exists, so look if the list has the file
			ArrayList<String> list= filesInFolders.get(currDir);
			if (! list.contains(file.getName()))
			{
				list.add(file.getName());
				//filesInFolders.put(file.getName(), list);
				//ReadForMatch(file);
				CreateNode(file); //New faster way, just create a node right away, read later
			}
			else  //Should never happen
				Driver.print("ERROR?:..File already exists in Dir: " +file.getName());
		}
		else //This is the first time we've entered this Directory
		{
			//Create our starting list of files in this dir
			ArrayList<String> list= new ArrayList<String>(); 
			list.add(file.getName());
			//ReadForMatch(file);
			CreateNode(file);
			filesInFolders.put(currDir, list);
		}
	}
	//Creates and adds a Node to our map if it hasn't been created
	private void CreateNode(File file) throws IOException 
	{
		//Driver.print("Found A Match in: "+file.getName() + " for Program:" + programName);
		Node child;
		if ( !map.containsKey(file.getName()))
		{
			//create node
			child= new Node(file.getName());
			map.put(file.getName(),child);
		}
	}
	public void FindReferences() 
	{
		//this.programName=programName.toLowerCase(); 
		int count=0;
		for (String fileName : pathsToFiles.keySet())
		{
			if (!fileName.equals(programName)) // probably need to fix extension?
			{	//System.out.println("Looking for :"+fileName+"  at Path: "+pathsTo.get(fileName));
				File file= new File(pathsToFiles.get(fileName)+"/"+fileName);
				//if (!file.isDirectory()) // only adding files to PathsTo
				{
					//Driver.print("We opened file:"+file.getName());
					//ReadForMatch(file);
					Long localStart =Instant.now().getEpochSecond();
					try {
						SearchALLOnce(file);
					} catch (IOException e) {
						Driver.print("Error when performing SearchALLOnce");
						Driver.print(" File name= "+file.getName());
					}
					Long localEnd =Instant.now().getEpochSecond();
					//System.out.println("("+count+") Tracing for:" +fileName+ " Time elapsed="+ (localEnd-localStart) +" seconds");
					++count;
				}

			}
		}

	}
	// Opens a File, and checks line by line if it contains any references to any known programs.
	private void SearchALLOnce(File file) throws IOException
	{
		//Driver.print("The Size of= "+pathsToFiles.size());

		//file.setWritable(true, false);
		if (file.getName().contains(".r") || file.getName().contains(".htm") ) //||file.getName().contains(".htm" )
		{	//if(programName.contains("fork0103x"))
			//	Driver.print(".....Looking at:"+file.getName() + "  searching for reference to program: " +programName);

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line= reader.readLine();
			boolean valid=line != null;
			while (line != null && valid)
			{ 
				for (String pName: pathsToFiles.keySet())
				{
					//Driver.print(line);
					if(line.contains(FixExtension(pName,true)))
					{
						//Driver.print("Found A Match in: "+file.getName() + " for Program:" + programName);
						Node child = map.get(file.getName());
						if(! FixExtension(child.getName(),true).equals(pName))
						{
							//Driver.print("We added "+programName+" to "+child.getName()+"'s references");
							child.AddReference(pName);
						}

						//Debugging
						/*if ( (pName.contains("LOAD0200".toLowerCase()) ) )
									{ if (file.getName().contains("LOAD0400".toLowerCase()))
											Driver.print("We found: " +pName +" in program: "+file.getName() );
									}*/
					}
				}
				line=reader.readLine();
				if(line!=null)
					line=line.toLowerCase();
			}
			reader.close();
		}

	}
	public String FixExtension(String s, boolean remove)
	{
		//Driver.print("Fixing Extension :: " +s + "  remove=" + remove);
		String searchable= s;
		if(remove) //Remove .r Extension
		{
			if(searchable.contains(".r"))
			{
				int index=searchable.indexOf('.');
				//Driver.print("Removed EXT: ");
				searchable= searchable.substring(0, index);
			}
		}
		else //Add .r extension
		{
			if(!searchable.contains(".r"))
				searchable=searchable+".r";
		}
		//Driver.print("returning::" +searchable);
		return searchable;
	}
	public void PrintAllFilesFound()
	{
		for( String key : pathsToFiles.keySet())
		{
			Driver.print(pathsToFiles.get(key)+"-->" +key);
		}
	}
	/** 
	 * Prints the mapping of programs and references
	 */
	public void PrintMap()
	{
		for (String key: map.keySet())
		{
			Driver.print("Program: "+key.toUpperCase() + " calls:");
			Node n= map.get(key);
			for(String ref: n.getReferences())
			{
				Driver.print("   "+ref);
			}
		}
	}

	public void FindSpecificKeys(String luaFile, String regionFile) throws IOException
	{
		ArrayList<String> initialList= new ArrayList<String>();
		ArrayList<String> keysGood= new ArrayList<String>();
		ArrayList<String> keysBad= new ArrayList<String>();
		ArrayList<String> regions = new ArrayList<String>();


		File file= new File(luaFile);
		if (file.isFile())
		{
			String good="_good";
			String bad="_bad";

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line= reader.readLine();
			boolean valid=line != null;
			while (line != null && valid)
			{ 
				if (line.contains(good) || line.contains(bad)) 
				{
					String str="";
					if (line.indexOf(good) != -1 )
					{
						str= line.substring(line.indexOf(good), line.length());
						if (str.indexOf("\"") !=-1 )
						{
							str= str.substring(0, str.indexOf("\""));
							if (!keysGood.contains(str))
								keysGood.add(str);

							str=str.substring(good.length()+1, str.length());

							if (!initialList.contains(str))
								initialList.add(str);
						}

					}
					else
					{
						str= line.substring(line.indexOf(bad), line.length());
						if (str.indexOf("\"") !=-1 )
						{
							str= str.substring(0, str.indexOf("\""));
							if (!keysBad.contains(str))
								keysBad.add(str);
						}
					}


				}
				line=reader.readLine();
			}
			reader.close();
		}


		//Used to make the initial list of keys found 
		//MakeMyList(initialList);

		//See if pass by ref works in java, it does 
		FindRegions( regionFile, regions);
		for(String r:regions) //Print Good Keys  
			for(String k : keysGood)
				Driver.print("3k_gov_"+r+k);

		for(String r:regions) //Print Bad Keys
			for(String k : keysBad)
				Driver.print("3k_gov_"+r+k);

		/* print all keys 
			for(String k : keysGood){Driver.print(k);}
			for(String k : keysBad){Driver.print(k);} 
		 */

	}
	public void FindRegions(String regionFile, ArrayList<String> regions) throws IOException
	{

		//FIND REGIONS 
		File file= new File(regionFile);
		if (file.isFile())
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line= reader.readLine();
			String index="main_";
			String capital="_capital";
			String resource="_resource";
			while(line!=null)
			{
				String str="";
				str= line.substring(line.indexOf(index)+index.length(), line.length());
				if( str.contains(capital))
					str= str.substring(0, str.indexOf(capital));
				else if (str.contains(resource))
					str= str.substring(0, str.indexOf(resource));


				//Driver.print("Found region= "+ str);
				if (!regions.contains(str))
					regions.add(str);

				line=reader.readLine();
			}
			reader.close();
		}
	}
	/**
	 * Oddly specific for governor events mods 
	 * @param keys
	 */
	public void MakeMyList(ArrayList<String> keys)
	{
		for(String k : keys)
		{
			Driver.print("_good_"+k);
		}
		for(String k : keys)
		{
			Driver.print("_bad_"+k);
		}
	}
	/**
	 * Takes in values to generate new Cdir_event_option_lines (works for dilemmas or incidents)
	 * @param startingIndex (int)
	 * @param eventKey  (string)
	 * @param targetChars (ArrayList<String>)
	 * @param targetFactions (ArrayList<String>)
	 * @param targetRegions (ArrayList<String>)
	 */
	public void OutputNewEventOptionLines(int startingIndex, String eventKey, ArrayList<String> targetChars,ArrayList<String> targetFactions,ArrayList<String> targetRegions, JTextArea output1, JTextArea output2)
	{
		String spacing1= "	";
		String spacing2= "	";
		String spacing3= "	";
		String spacing4= "	"; 
		int index= startingIndex;
		String s="";

		s+=((index++)+spacing1+eventKey+spacing2+"VAR_CHANCE"+spacing3+8000+spacing4+"default"+"\n");
		s+=((index++)+spacing1+eventKey+spacing2+"GEN_CND_SELF"+spacing3+""+spacing4+"target_faction_1"+"\n");
		//Ini the vars for factions 
		String target="target_faction_";
		int targetCount=1;
		String cndTarget="GEN_TARGET_FACTION";
		String cndTemplate="GEN_CND_FACTION";
		for(String k: targetFactions)
		{
			if(k.equals("default"))
			{
				s+=((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount)+"\n");
				targetCount++;
			}
			else
			{
				s+=((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount)+"\n");
				s+=((index++)+spacing1+eventKey+spacing2+cndTemplate+spacing3+k+spacing4+target+(targetCount)+"\n");
				targetCount++;
			}
		}
		//reset vars for chars 
		target="target_character_";
		targetCount=1;
		cndTarget="GEN_TARGET_CHARACTER";
		cndTemplate="GEN_CND_CHARACTER_TEMPLATE";
		for(String k: targetChars)
		{
			s+=((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount)+"\n");
			s+=((index++)+spacing1+eventKey+spacing2+cndTemplate+spacing3+k+spacing4+target+(targetCount)+"\n");
			targetCount++;
		}
		//reset for regions
		target="target_region_";
		targetCount=1;
		cndTarget="GEN_TARGET_REGION";
		cndTemplate="GEN_CND_REGION";
		for(String k: targetRegions)
		{
			s+=((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount)+"\n");
			s+=((index++)+spacing1+eventKey+spacing2+cndTemplate+spacing3+k+spacing4+target+(targetCount)+"\n");
			targetCount++;
		}
		output1.setText(s);
	}
	/**
	 *Deprecated Used for Testing, Prints to console
	 * @param startingIndex
	 * @param eventKey
	 * @param targetChars
	 * @param targetFactions
	 * @param targetRegions
	 */
	public void OutputNewEventOptionLines(int startingIndex, String eventKey, ArrayList<String> targetChars,ArrayList<String> targetFactions,ArrayList<String> targetRegions)
	{
		String spacing1= "	";
		String spacing2= "	";
		String spacing3= "	";
		String spacing4= "	"; 
		int index= startingIndex;


		Driver.print((index++)+spacing1+eventKey+spacing2+"VAR_CHANCE"+spacing3+8000+spacing4+"default");
		Driver.print((index++)+spacing1+eventKey+spacing2+"GEN_CND_SELF"+spacing3+""+spacing4+"target_faction_1");
		//Initialize vars for chars 
		String target="target_character_";
		int targetCount=1;
		String cndTarget="GEN_TARGET_CHARACTER";
		String cndTemplate="GEN_CND_CHARACTER_TEMPLATE";
		for(String k: targetChars)
		{
			Driver.print((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount));
			Driver.print((index++)+spacing1+eventKey+spacing2+cndTemplate+spacing3+k+spacing4+target+(targetCount));
			targetCount++;
		}
		//reset the vars for factions 
		target="target_faction_";
		targetCount=1;
		cndTarget="GEN_TARGET_FACTION";
		cndTemplate="GEN_CND_FACTION";
		for(String k: targetFactions)
		{
			Driver.print((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount));
			Driver.print((index++)+spacing1+eventKey+spacing2+cndTemplate+spacing3+k+spacing4+target+(targetCount));
			targetCount++;
		}
		target="target_region_";
		targetCount=1;
		cndTarget="GEN_TARGET_REGION";
		cndTemplate="GEN_CND_REGION";
		for(String k: targetRegions)
		{
			Driver.print((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount));
			Driver.print((index++)+spacing1+eventKey+spacing2+cndTemplate+spacing3+k+spacing4+target+(targetCount));
			targetCount++;
		}
	}

	/** 
	 * Takes in a path to a file that contains the dilemma payloads you want cloned, and a list of eventKeys you want to use.
	 * Prints the new payload lines with incremented indicies and event names.
	 * @param path (location+fileName.extension) File should include the lines of the payloads you want cloned
	 * @param eventKeys (list of new keys)
	 * @throws IOException
	 */
	public void OutputClonedEventLines(String path, ArrayList<String> eventKeys) throws IOException
	{
		File file= new File(path);
		if (file.isFile() )
		{
			int startingIndex=0;
			boolean firstLine=true;
			String spacing1="	";
			String initialEvent="";
			ArrayList<String> lines= new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line= reader.readLine();
			while (line != null )
			{ 
				if (line.equals(""))
					break;
				String noKey=line.substring(0,line.indexOf(spacing1));
				//Driver.print("nokey="+noKey);
				//first time through grab our starting Index
				if (firstLine)
				{
					startingIndex=Integer.parseInt(noKey);
					initialEvent=line.substring(noKey.length()+spacing1.length(), line.length());
					initialEvent=initialEvent.substring(0, initialEvent.indexOf(spacing1));
					//Driver.print("EventName="+initialEvent);
					firstLine=false;
				}
				lines.add(line);
				line=reader.readLine();
			}
			reader.close();
			startingIndex=startingIndex+lines.size();
			for(String keys: eventKeys)
			{
				for(String entry: lines)
				{
					//find and update the number id 
					String tmpEntry = entry.substring(entry.indexOf(initialEvent)+initialEvent.length(), entry.length());
					Driver.print((startingIndex++)+spacing1+keys+tmpEntry);
				}
			}

		}
	}
	private String DecideTargetKey(eTargetType target)
	{
		String s = null;
		if(target==eTargetType.Target_Character_1 || target==eTargetType.Target_Character_2)
			s="GEN_CND_CHARACTER_TEMPLATE";
		else if(target==eTargetType.Target_Faction_1 || target==eTargetType.Target_Faction_2)
			s="GEN_CND_FACTION";
		else if(target==eTargetType.Target_Region_1 || target==eTargetType.Target_Region_2)
			s="GEN_CND_REGION";
		return s;
	}
	private String DecideTargetIndex(eTargetType target)
	{
		String s = null;
		if(target==eTargetType.Target_Character_1)
			s="target_character_1";
		else if(target==eTargetType.Target_Character_2)
			s="target_character_2";
		else if(target==eTargetType.Target_Faction_1)
			s="target_faction_1";
		else if( target==eTargetType.Target_Faction_2)
			s="target_faction_2";
		else if(target==eTargetType.Target_Region_1)
			s="target_region_1";
		else if( target==eTargetType.Target_Region_2)
			s="target_region_2";
		return s;
	}
	public void OutputClonedEventLinesRaw(String Input, String eventKeys, eTargetType target, String type, String OptionalText, boolean overrideKey, JTextArea output1, JTextArea output2) 
	{
		//Driver.print("OutputClonedEventLinesRaw");
		int startingIndex=0;
		String spacing1="	";
		String initialEvent="";
		String initialPreface="";
		String ReplacementKey="";
		ArrayList<String> lines= new ArrayList<String>();
		String[] linesIn = Input.split("\n");
		String[] eventKeys1 = eventKeys.split("\n");
		String targetKey = DecideTargetKey(target);
		String targetIndex= DecideTargetIndex(target);

		//Driver.print("linesIN="+linesIn.length);
		//Driver.print("eventKeys1="+eventKeys1.length);
		//Driver.print("targetKey"+targetKey);
		//Driver.print("targetIndex"+targetIndex);
		String newKeys="";
		String finalReturn="";

		boolean firstLine=true;
		for(String keys: eventKeys1)
		{
			if(keys.equals(""))
				break;
			if(overrideKey)
				ReplacementKey= "";
			else 
			{
				if (targetKey.equals("GEN_CND_REGION"))
				{ //"3k_main_dongjun_capital" --> "dongjun" or "3k_main_dongjun_resource_1" -->"dongjun"
					int index= keys.indexOf("_capital");
					if (index==-1)
					{
						index= keys.indexOf("_resource");
						if(index==-1)
						{output1.setText("InvalidEntry for region, needs _capital or _resource");
						return;
						}
					}
					ReplacementKey=keys.substring(keys.indexOf("3k_main_")+8, index);
				}
				else if (targetKey.equals("GEN_CND_FACTION"))
				{ //"3k_main_faction_cao_cao" --> "cao_cao"
					int index= keys.indexOf("_faction");
					if(index==-1)
					{output1.setText("InvalidEntry for faction name, needs _faction");
					return;
					}
					ReplacementKey=keys.substring(keys.indexOf("_faction")+9, keys.length());
				}
				else if (targetKey.equals("GEN_CND_CHARACTER_TEMPLATE"))
				{ //"3k_main_template_historical_lu_bu_hero_fire" --> "lu_bu" 
					//"3k_dlc05_template_historical_lu_bu_hero_fire" --> "lu_bu" 
					String id="_template_historical_";
					int index= keys.indexOf(id);
					if (index==-1){output1.setText("InvalidEntry for Character, needs _template_historical");return;}
					else 
					{
						if (keys.indexOf("_hero")==-1){output1.setText("InvalidEntry for Character, needs _hero");return;}
					}
					ReplacementKey=keys.substring(index+id.length(), keys.indexOf("_hero"));
				}
				else
				{
					ReplacementKey= "TODO";
				}
			}

			for(int i =0; i< linesIn.length; ++i)
			{ 
				String line= linesIn[i];
				if (line.equals(""))
					break;
				String noKey=line.substring(0,line.indexOf(spacing1));
				//Driver.print("nokey="+noKey);		
				if (firstLine)//first time through grab our starting Index and event key preface
				{
					startingIndex=0;
					try{startingIndex=Integer.parseInt(noKey);}
					catch(NumberFormatException e){startingIndex=0;}

					initialEvent=line.substring(noKey.length()+spacing1.length(), line.length());
					initialEvent=initialEvent.substring(0, initialEvent.indexOf(spacing1));
					//Driver.print("EventName="+initialEvent);
					firstLine=false;
					if(overrideKey)
						initialPreface="";
					else
					{
					if(initialEvent.indexOf("_")==-1)
					{
						output1.setText("Cloned key formatting incorrect, needs format similar to '3k_something_name'");return;}
						else if (initialEvent.indexOf("_")== initialEvent.lastIndexOf("_"))
						{output1.setText("Cloned key formatting incorrect, needs format similar to '3k_something_name'");return;}
						initialPreface= initialEvent.substring(initialEvent.indexOf("_")+1, initialEvent.lastIndexOf("_"));
						initialPreface= initialPreface.substring(initialPreface.indexOf("_")+1, initialPreface.length());
						initialPreface=initialEvent.substring(0, initialEvent.indexOf(initialPreface));
						//Driver.print("initialPreface="+initialPreface);
					}
				}
				//Check if we need to replace anything
				if(line.contains(targetKey) && line.contains(targetIndex))
				{
					//Driver.print("REPLACE LINE= "+line);
					//find whats in the middle of them 
					String lineBefore=line.substring(0, line.indexOf(targetKey)+targetKey.length());
					//Driver.print("LINEBEFORE= "+lineBefore);
					String lineAfter=line.substring(line.indexOf(targetIndex), line.length());
					//Driver.print("LINEAFTER= "+lineAfter);
					line= lineBefore+spacing1+keys+spacing1+lineAfter;
					//Driver.print("LINE FINAL= "+line);
				}
				//Add to our modified list 
				lines.add(line);
			}
			//Create our new starting Index
			startingIndex=startingIndex+lines.size();
			for(String entry: lines)
			{
				//find and update the number id 
				String tmpEntry = entry.substring(entry.indexOf(initialEvent)+initialEvent.length(), entry.length());
				//Driver.print("TMPENTRY="+tmpEntry);
				//Driver.print("initialPreface"+initialPreface);
				//Driver.print("ReplacementKey"+ReplacementKey);
				//Driver.print("startingIndex"+startingIndex++);
				//Driver.print((startingIndex++)+spacing1+initialPreface+OptionalText+ReplacementKey+"_"+type+tmpEntry);
				
				finalReturn += ((startingIndex++)+spacing1+initialPreface+OptionalText+ReplacementKey+"_"+type+spacing1+tmpEntry+"\n");
			}
			newKeys += initialPreface+OptionalText+ReplacementKey+"_"+type+"\n";

		}
		output1.setText(newKeys);
		output2.setText(finalReturn);
	}

	public String OutputIncidentTitleTXT(String eventName)
	{
		String title="3k_main_title_";
		String spacing1= "	"; //double check 
		//Driver.print(title+eventName+spacing1+"[PH]Title");
		return title+eventName+spacing1+"[PH]Title"+"\n";
	}
	public String OutputIncidentDescTXT(String eventName)
	{
		String description="3k_main_description_";
		String spacing1= "	"; //double check 
		//Driver.print(description+eventName+spacing1+"[PH]Desc");
		return description+eventName+spacing1+"[PH]Desc"+"\n";
	}
	public String OutputIncidentTXT(ArrayList<String> eventNames)
	{	String s="";
	for(String event: eventNames)
		s+=OutputIncidentTitleTXT(event);
	for(String event: eventNames)
		s+=OutputIncidentDescTXT(event);
	return s;
	}

	public void OutputIncidentTXT(String[] eventNames ,JTextArea output1, JTextArea output2)
	{	
		String s="";
		for(String event: eventNames)
			s+=OutputIncidentTitleTXT(event);
		output1.setText(s);
		s="";
		for(String event: eventNames)
			s+=OutputIncidentDescTXT(event);
		output2.setText(s);
	}
	public String OutputDilemmaMainTXT(String eventName, int choices)
	{	String s="";
	String title="3k_main_title_";
	String description="3k_main_description_";
	String spacing1= "	"; //double check 
	//Driver.print(title+eventName+spacing1+"[PH]Title");
	s+=(title+eventName+spacing1+"[PH]Title"+"\n");
	//Driver.print(description+eventName+spacing1+"[PH]Desc");
	s+=(description+eventName+spacing1+"[PH]Desc"+"\n");
	//Driver.print(choiceTitle+eventName+"FIRST"+spacing1+"[PH]button_title");

	return s;
	}
	public String OutputDilemmaChoiceTXT(String eventName, int choices)
	{	String s="";
	String choiceTitle="3k_main_choice_title_";
	String choiceLabel="3k_main_choice_label_";
	String spacing1= "	"; //double check 
	//Choices
	s+=(choiceTitle+eventName+"FIRST"+spacing1+"[PH]button_title"+"\n");
	if(choices>1)
		s+=(choiceTitle+eventName+"SECOND"+spacing1+"[PH]button_title"+"\n");
	if(choices>2)
		s+=(choiceTitle+eventName+"THIRD"+spacing1+"[PH]button_title"+"\n");
	if(choices>3)
		s+=(choiceTitle+eventName+"FOURTH"+spacing1+"[PH]button_title"+"\n");
	//labels
	s+=(choiceLabel+eventName+"FIRST"+spacing1+"[PH]button_label"+"\n");
	if(choices>1)
		s+=(choiceLabel+eventName+"SECOND"+spacing1+"[PH]button_label"+"\n");
	if(choices>2)
		s+=(choiceLabel+eventName+"THIRD"+spacing1+"[PH]button_label"+"\n");
	if(choices>3)
		s+=(choiceLabel+eventName+"FOURTH"+spacing1+"[PH]button_label"+"\n");

	return s;
	}
	public String OutputDilemmaTXT(ArrayList<String> eventNames, int choices )
	{
		String s="";
		for(String event: eventNames)
			s+=OutputDilemmaMainTXT(event, choices);
		for(String event: eventNames)
			s+=OutputDilemmaChoiceTXT(event, choices);
		return s;
	}
	public void OutputDilemmaTXT(String[] eventNames, int choices ,JTextArea output1, JTextArea output2)
	{
		String s="";
		for(String event: eventNames)
			s+=OutputDilemmaMainTXT(event, choices);
		output1.setText(s);
		s="";
		for(String event: eventNames)
			s+=OutputDilemmaChoiceTXT(event, choices);
		output2.setText(s);
	}
	public static eTargetType eTargetType(int selectedIndex) {
		return eTargetType.values()[selectedIndex];
	}


}
