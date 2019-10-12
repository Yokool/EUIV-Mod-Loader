package com.Gamer;

import java.io.File;
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
	
	// Contructor, Param used to init the path to Documents
	public FileLoader(File pathToModFolder) {
		
		this.pathToModFolder = pathToModFolder;
		LoadAllMods();
		FindDescriptors();
	}
	
	
	// A method to load all mods
	private void LoadAllMods() {
		
		AllMods = this.pathToModFolder.listFiles();
		
		ModNames = new String[AllMods.length];
		
		System.out.println("Found " + AllMods.length + " mods.");
		
		for(int i = 0; i < AllMods.length; i++) {
			
			ModNames[i] = AllMods[i].getName();
			System.out.println(ModNames[i]);
			
		}
		
	}
	
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
	
}
