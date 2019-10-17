package com.Gamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileLoader {
	
	// A class used to load all the mod directiories
	
	// The directorie where all your mods are, Documents/EUIV...
	private File pathToModFolder;
	
	// An array holding all the mod files
	private File[] AllMods;
	
	// Strings holding all the names of the mod files
	private String[] ModNames;
	
	private ArrayList<File> Descriptors = new ArrayList<File>();
	
	private ArrayList<Descriptor> MovedDescriptors = new ArrayList<Descriptor>();
	
	// Contructor, Param used to init the path to Documents
	public FileLoader(File pathToModFolder) {
		
		this.pathToModFolder = pathToModFolder;
		LoadAllMods();
		FindDescriptors();
		MoveDescriptors();
		RenameArchivePath();
	}
	
	
	// A method to:
	// - load a list of all files in the pathToModFolder
	// - store all the file names as strings and display them
	// - output how many folders were found
	// ! to avoid confusion AllMods refers to all folders in the directory pathToModFolder !
	private void LoadAllMods() {
		
		AllMods = this.pathToModFolder.listFiles();
		
		ModNames = new String[AllMods.length];
		
		System.out.println("Found " + AllMods.length + " folders.");
		
		for(int i = 0; i < AllMods.length; i++) {
			
			ModNames[i] = AllMods[i].getName();
			System.out.println(ModNames[i]);
			
		}
		
	}
	
	// A method which:
	// - looks for all descriptor.mod files in AllMods array
	// - stores said file in Descriptors array list
	// - output how many descriptors were found out of the number of all the mods/folders
	private void FindDescriptors() {
		
		for(File mod : AllMods) {
			
			if(mod.isDirectory()) {
				
				File[] tempFiles = mod.listFiles();
				
				for(File tempFile : tempFiles) {
					
					if(tempFile.getName().equals("descriptor.mod")) {
						Descriptors.add(tempFile);
						
					}
					
				}
				
				
			}
			
		}
		
		System.out.println("Found " + Descriptors.size() + " descriptors out of " + AllMods.length + " mods.");
		
	}
	
	// Moves all descriptors from their directory to pathToModFolder directory
	private void MoveDescriptors(){
		
		for(File descriptor : Descriptors) {
			
			MoveFile(descriptor);
			
		}
		
		
	}
	
	// helper function to:
	// - Move a single file to pathToModFolder path
	// - Rename the moved file to the name of the file the descriptor was located in (most of the time this will be the name of the mod)
	// - Add the moved file to the MovedDescriptors array list if the moving succeeds
	// - Output a message to the console if the moving fails
	private void MoveFile(File file) {
		String newName = file.getParentFile().getName();
		File movedDescriptor = new File(pathToModFolder.getPath() + "\\" + newName + ".mod");
		
		if(file.renameTo(movedDescriptor)) {
			
			MovedDescriptors.add(new Descriptor(movedDescriptor, null));
			
		}else {
			System.out.println("Failed to move " + file);
		}
		
		
		
	}
	
	private void RenameArchivePath() {
		
		for(Descriptor descriptor : MovedDescriptors) {

			ReadFile(descriptor);
			
		}
		
	}
	
	private void ReadFile(Descriptor file) {
		String[] lineSplit = null;
		String Name = null;
		
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file.descriptor));
			
			String line;
			
			while ( (line = reader.readLine() ) != null) {
				
				if(line.regionMatches(true, 0, "archive", 0, 7)) {
					
					lineSplit = line.split("\"");
					
					Name = lineSplit[1].replace("mod/", "");
					
					
					
				}
				
				
			}
			

			reader.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {
			File move = new File(pathToModFolder.getPath() + "\\" + Name + ".mod");
			System.out.println(move);
			file.descriptor.renameTo(move);
			
		}
		
		
	}
	
	
	
	
}



class Descriptor{
	
	public File zipMod = null;
	public File descriptor = null;
	
	public Descriptor(File desc, File zip) {
		
		this.descriptor = desc;
		this.zipMod = zip;
		
	}
	
}
