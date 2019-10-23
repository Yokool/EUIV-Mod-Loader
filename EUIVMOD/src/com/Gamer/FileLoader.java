package com.Gamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileLoader {
	
	// A class used to load all the mod directiories
	
	// The directorie where all your mods are, Documents/EUIV...
	private File pathToModFolder;
	
	// An array holding all the mod files
	private File[] AllMods;
	
	// Strings holding all the names of the mod files
	private String[] ModNames;
	
	private ArrayList<Descriptor> Descriptors = new ArrayList<Descriptor>();
	
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
						Descriptors.add(new Descriptor(tempFile, mod));
						
					}
					
				}
				
				
			}
			
		}
		
		System.out.println("Found " + Descriptors.size() + " descriptors out of " + AllMods.length + " mods.");
		
	}
	
	// Moves all descriptors from their directory to pathToModFolder directory
	private void MoveDescriptors(){
		
		for(Descriptor descriptor : Descriptors) {
			
			MoveFile(descriptor.descriptor, descriptor.zipMod);
			
		}
		
		
	}
	
	// helper function to:
	// - Move a single file to pathToModFolder path
	// - Rename the moved file to the name of the file the descriptor was located in (most of the time this will be the name of the mod)
	// - Add the moved file to the MovedDescriptors array list if the moving succeeds
	// - Output a message to the console if the moving fails
	private void MoveFile(File descriptor, File modArchive) {
		String newName = descriptor.getParentFile().getName();
		File movedDescriptor = new File(pathToModFolder.getPath() + "\\" + newName + ".mod");
		
		if(descriptor.renameTo(movedDescriptor)) {
			
			MovedDescriptors.add(new Descriptor(movedDescriptor, modArchive));
			
		}else {
			System.out.println("Failed to move " + descriptor);
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
					Name = Name.replace(".zip", "");
					
					
				}
				
				
			}
			

			reader.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {
			File move = new File(pathToModFolder.getPath() + "\\" + Name + ".mod");
			
			file.descriptor.renameTo(move);
			
			System.out.println(file.zipMod);
			ZipFile(file.zipMod, Name);
			
		}
		
		
	}
	
	
	public void ZipFile(File file, String file_name) {
		
		try {
			
			pack(file.toString(), file.getParentFile().getPath() + "\\" + file_name + ".zip");
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	// Totally not stolen from stack overflow
	public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
	    Path p = Files.createFile(Paths.get(zipFilePath));
	    try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
	        Path pp = Paths.get(sourceDirPath);
	        Files.walk(pp)
	          .filter(path -> !Files.isDirectory(path))
	          .forEach(path -> {
	              ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
	              try {
	                  zs.putNextEntry(zipEntry);
	                  Files.copy(path, zs);
	                  zs.closeEntry();
	            } catch (IOException e) {
	                System.err.println(e);
	            }
	          });
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
