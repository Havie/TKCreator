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
	
	// For OutputNewEventOptionLines , hopefully spacing doesnt change for other methods?
	final String spacing1= "	";
	final String spacing2= "	";
	final String spacing3= "	";
	final String spacing4= "	"; 

	public enum eTargetType {Target_Region_1, Target_Region_2, Target_Character_1, Target_Character_2, Target_Faction_1,Target_Faction_2, KEY_SEARCH};

	public Parser()
	{
		map=new HashMap<String,Node>();
		newPrograms=new HashMap<String,Node>();
	}
	public static eTargetType eTargetType(int selectedIndex) {
		return eTargetType.values()[selectedIndex];
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
		int index= startingIndex;
		String s="";
		s+=HandleFactions(targetFactions,index, eventKey);
		s+=HandleCharacters(targetChars, index, eventKey);
		s+=HandleRegions(targetRegions, index, eventKey);
		output1.setText(s);
	}
	private String HandleFactions(ArrayList<String> targetFactions, int index, String eventKey)
	{
		int targetCount=1;
		String target="target_faction_";
		String cndTarget="GEN_TARGET_FACTION";
		String cndTemplate="GEN_CND_FACTION";
		String s="";
		//DEFAULTS
		s+=((index++)+spacing1+eventKey+spacing2+"VAR_CHANCE"+spacing3+8000+spacing4+"default"+"\n");
		s+=((index++)+spacing1+eventKey+spacing2+"GEN_CND_SELF"+spacing3+""+spacing4+"target_faction_1"+"\n");

		for(String k: targetFactions)
		{
			if(k.equals("default"))
			{
				s+=((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount)+"\n");
				targetCount++;
			}
			else
				s+=CreateLine( index,  eventKey, cndTarget,  cndTemplate ,  target,  targetCount,  k );
		}
		return s;
	}
 	private String HandleCharacters(ArrayList<String> targetChars, int index, String eventKey)
 	{
 		//Vars for Chars
		String target="target_character_";
		int targetCount=1;
		String cndTarget="GEN_TARGET_CHARACTER";
		String cndTemplate="GEN_CND_CHARACTER_TEMPLATE";
		String s="";
		for(String k: targetChars)
			s+=CreateLine( index,  eventKey, cndTarget,  cndTemplate ,  target,  targetCount,  k );
		
		return s;
		
 	}
	private String HandleRegions(ArrayList<String> targetRegions, int index, String eventKey)
	{
		//Vars for regions
		String target="target_region_";
		int targetCount=1;
		String cndTarget="GEN_TARGET_REGION";
		String cndTemplate="GEN_CND_REGION";
		String s= "";
		for(String k: targetRegions)
			s+=CreateLine( index,  eventKey, cndTarget,  cndTemplate ,  target,  targetCount,  k );
		
		return s;
	}
 	private String CreateLine(int index, String eventKey, String cndTarget, String cndTemplate , String target, int targetCount, String key )
	{
		String s="";
		s+=((index++)+spacing1+eventKey+spacing2+cndTarget+spacing3+""+spacing4+target+(targetCount)+"\n");
		s+=((index++)+spacing1+eventKey+spacing2+cndTemplate+spacing3+key+spacing4+target+(targetCount)+"\n");
		++targetCount;
		return s;
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
		else if (target==eTargetType.KEY_SEARCH)
			s="KEY_SEARCH";
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
		else if (target==eTargetType.KEY_SEARCH)
			s="KEY_SEARCH";
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
		String lastReplacementKey=""; //Used by Clone raw input text keySearch
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
		boolean skip=false;
		boolean keySearch=false;

		boolean firstLine=true;
		for(String keys: eventKeys1)
		{
			skip=false;
			if(keys.equals(""))
				skip=true;			
			if(!skip)
			{
				if((DecideTargetKey(target).equals("KEY_SEARCH")))
						keySearch=true;
				else
					VerifyConditions( skip, overrideKey,  keys,  target,  targetKey,  ReplacementKey,  output1);

				for(int i =0; i< linesIn.length; ++i)
				{ 	
					String line= linesIn[i];
					if (line.equals(""))
						break;

					if(keySearch&&!firstLine)
					{
						//Check if the line contains the string to replace
						//replace it 
						//make it so the #s are separate from the rest of the line.
						if(line.contains(OptionalText))
						{
							line=line.replace(OptionalText, keys);
							initialPreface="";
							initialEvent= spacing1;
							//used for new keys  //overrideKey ?
							String noKey=line.substring(0,line.indexOf(spacing1));
							ReplacementKey=line.substring(noKey.length()+spacing1.length(), line.length());
							ReplacementKey=ReplacementKey.substring(0, ReplacementKey.indexOf(spacing1))+"\n";
						}
						//always add line for final print out
						lines.add(line);
					}

					else if(firstLine)
					{
						if(overrideKey && keySearch)
						{
							if(line.contains(OptionalText))
								line=line.replace(OptionalText, keys);
							//always add line for final print out
							lines.add(line);
						}
						else
						{
							//1000 3k_main_  gets the #1000
							String noKey=line.substring(0,line.indexOf(spacing1));
							//Driver.print("nokey="+noKey);		
							if (firstLine)//first time through grab our starting Index and event key preface
							{
								startingIndex=0;
								try{startingIndex=Integer.parseInt(noKey);}
								catch(NumberFormatException e){startingIndex=0;}
								startingIndex+=linesIn.length;
								//Driver.print("STRTING INDEX="+startingIndex);
								//Gets 1000 3k_main_whatever ==> 3k_main_whatever
								initialEvent=line.substring(noKey.length()+spacing1.length(), line.length());
								initialEvent=initialEvent.substring(0, initialEvent.indexOf(spacing1));
								//Driver.print("EventName="+initialEvent);
								firstLine=false;
								if(overrideKey) // if we are over riding the whole key, handle that at the end
									initialPreface="";
								else
								{
									if (keySearch)
									{
										//Driver.print("LinesIn Size=" +linesIn.length);
										if(line.contains(OptionalText))
										{
											line=line.replace(OptionalText, keys);
											initialPreface="";
											initialEvent= spacing1;
											//used for new keys 
											ReplacementKey=line.substring(noKey.length()+spacing1.length(), line.length());
											ReplacementKey=ReplacementKey.substring(0, ReplacementKey.indexOf(spacing1))+"\n";
										}
									}
									else
									{
										if(initialEvent.indexOf("_")==-1)
											{output1.setText("Cloned key formatting incorrect, needs format similar to '3k_something_name'");return;}
										else if (initialEvent.indexOf("_")== initialEvent.lastIndexOf("_"))
											{output1.setText("Cloned key formatting incorrect, needs format similar to '3k_something_name'");return;}
										//Get the first 3k_main_whatever_name  ==> whatever_name  (gets rid of first two _'2 aka 3k_main_ )
										initialPreface= initialEvent.substring(initialEvent.indexOf("_")+1, initialEvent.lastIndexOf("_"));
										initialPreface= initialPreface.substring(initialPreface.indexOf("_")+1, initialPreface.length());
										initialPreface=initialEvent.substring(0, initialEvent.indexOf(initialPreface));
										//Driver.print("initialPreface="+initialPreface);

									}
								}
							}
							//Check if we need to replace anything, and add to list for later
							CheckLine( line, keys,  spacing1,  targetKey, targetIndex,lines);

						}
					}
					else
						CheckLine( line, keys,  spacing1,  targetKey, targetIndex,lines);
					//Create our new starting Index
					//startingIndex+=lines.size();
					String extraAppend="_";


					for(String entry: lines)
					{	
						String tmpEntry = entry.substring(entry.indexOf(initialEvent)+initialEvent.length(), entry.length());

						//find and update the number id 
						//Driver.print("initialEvent="+initialEvent);
						//Driver.print("TMPENTRY="+tmpEntry);
						//Driver.print("initialPreface"+initialPreface);
						//Driver.print("ReplacementKey"+ReplacementKey);
						//Driver.print("startingIndex"+startingIndex);
						//Driver.print((startingIndex++)+spacing1+initialPreface+OptionalText+ReplacementKey+"_"+type+tmpEntry);
						//Driver.print("KEeySearch="+keySearch);
						if(overrideKey)
						{
							if(keySearch)
								finalReturn += (entry+"\n");
							else //same refactor
								finalReturn += ((startingIndex++)+spacing1+initialPreface+OptionalText+ReplacementKey+extraAppend+type+tmpEntry+"\n");

						}
						else
						{
							if(keySearch)
								finalReturn += ((startingIndex++)+spacing1+tmpEntry+"\n");
							else //same refactor
								finalReturn += ((startingIndex++)+spacing1+initialPreface+OptionalText+ReplacementKey+extraAppend+type+tmpEntry+"\n");
						}
					}
					if(!keySearch)
					{
						String tmp=initialPreface+OptionalText+ReplacementKey+extraAppend+type+"\n";
						if(!lastReplacementKey.equals(tmp))
						{
							lastReplacementKey=tmp;
							newKeys += tmp;
						}
					}
					else
					{  // why this first empty string check?
						if(lastReplacementKey.equals("") || !lastReplacementKey.equals(ReplacementKey))	
						{
							newKeys+=ReplacementKey;
							lastReplacementKey=ReplacementKey;
						}
					}
					lines.clear();
				}
			}
			output1.setText(newKeys);
			output2.setText(finalReturn);
		}
	}
	//This logic was becoming too big to look at, has to separate out.
	private void VerifyConditions(boolean skip, boolean overrideKey, String key, eTargetType target, String targetKey, String ReplacementKey, JTextArea output1)
	{
		if(overrideKey)
			ReplacementKey= "";
		else 
		{
			if (targetKey.equals("GEN_CND_REGION"))
			{ 
				if (key.contains("_sea_")) 
					{output1.setText("Skip seas/rivers, " +key);skip=true;}
				
				if(!skip)
				{
					//"3k_main_dongjun_capital" --> "dongjun" or "3k_main_dongjun_resource_1" -->"dongjun"
					int index= key.indexOf("_capital");
					if (index==-1)
					{
						index= key.indexOf("_resource");
						if(index==-1)
						{
							if(key.indexOf("_pass")!=-1)
								skip=true;
							else 
								{output1.setText("InvalidEntry for region, needs _capital or _resource: "+key);return;}
						}
						else
							{output1.setText("TMP turned off for resource, " +key);skip=true;}
					}
				}
				int offset=8;
				int index2=key.indexOf("3k_main_");
				if(index2==-1)
				{
					offset=9;	
					//index2=key.indexOf("3k_dlc06_");	
					index2=key.indexOf("3k_dlc"); // should hopefully work for future DLC
				}
				if(index2==-1)  
					{output1.setText("InvalidEntry for region, needs prefix '3k_main' or '3k_dlc06:  '"+key);return;}
				ReplacementKey=key.substring(index2+offset, key.length());
			}
			else if (targetKey.equals("GEN_CND_FACTION"))
			{   //"3k_main_faction_cao_cao" --> "cao_cao"
				int index= key.indexOf("_faction");
				if(index==-1)
					{output1.setText("InvalidEntry for faction name, needs _faction");return;}
				ReplacementKey=key.substring(key.indexOf("_faction")+9, key.length());
			}
			else if (targetKey.equals("GEN_CND_CHARACTER_TEMPLATE"))
			{   //"3k_main_template_historical_lu_bu_hero_fire" --> "lu_bu" 
				//"3k_dlc05_template_historical_lu_bu_hero_fire" --> "lu_bu" 
				String id="_template_historical_";
				int index= key.indexOf(id);
				if (index==-1)
					{output1.setText("InvalidEntry for Character, needs _template_historical "+key);return;}
				else if (key.indexOf("_hero")==-1)
					{output1.setText("InvalidEntry for Character, needs _hero "+key);return;}
				ReplacementKey=key.substring(index+id.length(), key.indexOf("_hero"));
			}
			else
				ReplacementKey= "TODO";
		}
	}

	private void CheckLine(String line,String keys, String spacing1, String targetKey,String targetIndex, ArrayList<String> lines)
	{
		//Check if we need to replace anything
		if(line.contains(targetKey) && line.contains(targetIndex))
		{
			//Driver.print("TARGET KEY= "+targetKey);
			//Driver.print("TargetIndex= "+targetIndex);
			//Driver.print("REPLACE LINE= "+line);
			//find whats in the middle of them 
			String lineBefore=line.substring(0, line.indexOf(targetKey)+targetKey.length());
			//Driver.print("LINEBEFORE= "+lineBefore);
			String lineAfter=line.substring(line.indexOf(targetIndex), line.length());
			//Driver.print("LINEAFTER= "+lineAfter);
			//if(keySearch)
			//	line= lineBefore+keys+lineAfter;
			//else
			line= lineBefore+spacing1+keys+spacing1+lineAfter;
			//Driver.print("LINE FINAL= "+line);
		}
		//Add to our modified list 
		lines.add(line);
	}
	//INCIDENTS - NEW
	public String OutputIncidentTitleTXT(String eventName, String titleTXT)
	{
		String title="incidents_localised_title_";
		String spacing1= "	"; //double check 
		//Driver.print(title+eventName+spacing1+"[PH]Title");
		return title+eventName+spacing1+titleTXT+"\n";
	}
	public String OutputIncidentDescTXT(String eventName, String descTXT)
	{
		String description="incidents_localised_description_";
		String spacing1= "	"; //double check 
		//Driver.print(description+eventName+spacing1+"[PH]Desc");
		return description+eventName+spacing1+descTXT+"\n";
	}
	public String OutputIncidentTXT(ArrayList<String> eventNames)
	{	String s="";
	for(String event: eventNames)
		s+=OutputIncidentTitleTXT(event, "[PH]Title");
	for(String event: eventNames)
		s+=OutputIncidentDescTXT(event, "[PH]Desc");
	return s;
	}
	//DILEMMAS - NEW
	public void OutputIncidentTXT(String[] eventNames ,JTextArea output1, JTextArea output2, String titleText, String descText)
	{	
		String s="";
		for(String event: eventNames)
			s+=OutputIncidentTitleTXT(event, titleText);
		output1.setText(s);
		s="";
		for(String event: eventNames)
			s+=OutputIncidentDescTXT(event, descText);
		output2.setText(s);
	}
	public String OutputDilemmaMainTXT(String eventName, int choices, String titleText, String descText)
	{	String s="";
	String title="dilemmas_localised_title_";
	String description="dilemmas_localised_description_";
	String spacing1= "	"; //double check 
	//Driver.print(title+eventName+spacing1+"[PH]Title");
	s+=(title+eventName+spacing1+titleText+"\n");
	//Driver.print(description+eventName+spacing1+"[PH]Desc");
	s+=(description+eventName+spacing1+descText+"\n");
	//Driver.print(choiceTitle+eventName+"FIRST"+spacing1+"[PH]button_title");

	return s;
	}
	public String OutputDilemmaChoiceTXT(String eventName, int choices)
	{	String s="";
	String choiceTitle="cdir_events_dilemma_choice_details_localised_choice_title_";
	String choiceLabel="cdir_events_dilemma_choice_details_localised_choice_label_";
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
	public String OutputDilemmaTXT(ArrayList<String> eventNames, int choices,String titleText, String descText )
	{
		String s="";
		for(String event: eventNames)
			s+=OutputDilemmaMainTXT(event, choices, titleText,  descText);
		for(String event: eventNames)
			s+=OutputDilemmaChoiceTXT(event, choices);
		return s;
	}
	public void OutputDilemmaTXT(String[] eventNames, int choices ,JTextArea output1, JTextArea output2,String titleText, String descText)
	{
		String s="";
		for(String event: eventNames)
			s+=OutputDilemmaMainTXT(event, choices,  titleText,  descText);
		output1.setText(s);
		s="";
		for(String event: eventNames)
			s+=OutputDilemmaChoiceTXT(event, choices);
		output2.setText(s);
	}
	

	/*
	 * KEY                                           Char Skill node set Key                   Char Skill Key                       blank(Faction key,campaign) TIER=3  indent=decimal blank(subculture) pointsOnCreation, Visible in UI, GameMode, true
3k_main_skillset_historical_liu_bei_3_romance	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_water_archer_2_flexibility_mlvl_3			3	2.9000000953674316		0	1	true	romance	true
3k_main_skillset_historical_liu_bei_3D	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_earth_5_meditation_mlvl_3			3	2.200000047683716		0	1	true		true
3k_main_skillset_historical_liu_bei_3B	3k_main_skillset_historical_liu_bei	3k_main_ability_commander			3	0.800000011920929		1	1	true		true
3k_main_skillset_historical_liu_bei_3A	3k_main_skillset_historical_liu_bei	3k_custom_liu_people			3	0.10000000149011612		0	1	true		true
3k_main_skillset_historical_liu_bei_2E_romance	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_earth_2_mobility_mlvl_3			1	2.9000000953674316		0	1	true	romance	true
3k_main_skillset_historical_liu_bei_0A	3k_main_skillset_historical_liu_bei	3k_main_skill_int_clone			2	0.10000000149011612		0	1	true		true
3k_main_skillset_historical_liu_bei_0B	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_metal_4_understanding_mlvl_3			2	0.800000011920929		0	1	true		true
3k_main_skillset_historical_liu_bei_0C	3k_main_skillset_historical_liu_bei	3k_commander_dignity_clone			2	1.5		0	1	true		true
3k_main_skillset_historical_liu_bei_0D	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_metal_3_zeal_mlvl_3			2	2.200000047683716		0	1	true		true
3k_main_skillset_historical_liu_bei_0E	3k_main_skillset_historical_liu_bei	3k_main_skill_special_ability_earth_natures_ally			2	2.9000000953674316		0	1	true		true
3k_main_skillset_historical_liu_bei_1A	3k_main_skillset_historical_liu_bei	3k_main_liu_changban			0	0.10000000149011612		1	1	true		true
3k_main_skillset_historical_liu_bei_1B	3k_main_skillset_historical_liu_bei	3k_main_cao_cao			0	0.800000011920929		1	1	true		true
3k_main_skillset_historical_liu_bei_1C	3k_main_skillset_historical_liu_bei	3k_main_skill_special_ability_earth_stone_bulwark			0	1.5		1	1	true		true
3k_main_skillset_historical_liu_bei_1D	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_water_strategist_3_composure_mlvl_3			0	2.200000047683716		0	1	true		true
3k_main_skillset_historical_liu_bei_1E	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_water_archer_4_perception_mlvl_3			0	2.9000000953674316		0	1	true		true
3k_main_skillset_historical_liu_bei_2C_romance	3k_main_skillset_historical_liu_bei	3k_main_boost			1	1.5		0	1	true	romance	true
3k_main_skillset_historical_liu_bei_2A_romance	3k_main_skillset_historical_liu_bei	3k_main_skill_special_ability_earth_inspiring_words			1	0.10000000149011612		1	1	true	romance	true
3k_main_skillset_historical_liu_bei_2B	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_fire_5_dignity_mlvl_3			1	0.800000011920929		0	1	true		true
3k_main_skillset_historical_liu_bei_2D	3k_main_skillset_historical_liu_bei	3k_main_skill_mastery_fire_1_intensity_mlvl_3			1	2.200000047683716		0	1	true		true
	 */
	
	
	
	
	/* OLD METHODS (Non GUI)
	 * These methods were used for oddly specific behavior by me
	 * and ran through the IDE driver, not the GUI application.
	 * The logic here was adapted from a different project I made,
	 * and best use for TW is to search through the users workshop folders
	 * and output a list of mod file names so you don't have to go and open
	 * every folder and inspect or try to global search by keywords.
	 * I might include some version of this functionality in the GUI.
	 * */
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
	/** DEPRECATED
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
}
