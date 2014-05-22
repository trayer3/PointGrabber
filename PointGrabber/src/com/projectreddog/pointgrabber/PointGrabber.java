package com.projectreddog.pointgrabber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PointGrabber extends JavaPlugin implements CommandExecutor {
	
	/**
	 *  File writing variables.
	 */
	FileWriter fileWriter;
	BufferedWriter buffWriter;
	String path = "plugins/PointGrabberFiles";
	
	/**
	 *  General Variables
	 */
	int currentPVPPointNum = 1;
	int currentCapturePointNum = 1;
	
	@Override
	public void onEnable(){
		/**
		 *  Logic to be performed when the plugin is enabled
		 *   - Set up to save to file.
		 *      -- Just add to the file each time.
		 */

		try {
 
			File file = new File(path + "/config.txt");
 
			/**
			 *  If file doesn't exists, then create it.
			 */
			if (!file.exists()) {
				file.createNewFile();
			}
 
			fileWriter = new FileWriter(file.getAbsoluteFile());
			buffWriter = new BufferedWriter(fileWriter);
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/**
		 *  Suggest adding a new section.
		 */
		Bukkit.broadcastMessage("PointGrabber suggests:  Add a new section?  Do /pointnewsection");
	}

	@Override
	public void onDisable() {
		/**
		 *  Close file writer.
		 */
		try {
			buffWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		Location location;
		
		if ( sender instanceof Player ){
			/**
			 *  Only players can run this command.
			 */
			
			if (cmd.getName().equalsIgnoreCase("pointpvp")){
			
				location = ((Player) sender).getLocation();
				
				if( args.length == 0 )
				{
					/**
					 *  Grab only the X, Y, and Z of Point with Yaw.
					 */
					writePointNum(currentPVPPointNum);
					currentPVPPointNum++;
					writeXYZ(location, "yes", "    ");
				}
				else if( args.length == 1 )
				{
					if( args[0].equalsIgnoreCase("noyaw") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") )
					{
						/**
						 *  PVP Point creation with Yaw not included.
						 */
						writePointNum(currentPVPPointNum);
						currentPVPPointNum++;
						writeXYZ(location, "no", "    ");
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
				
				return true;  // Command successful
			}
			
			if (cmd.getName().equalsIgnoreCase("pointcapture")){
				
				location = ((Player) sender).getLocation();
				
				if( args.length == 3 )
				{
					/**
					 *  Radius, Display Name, Capture Time
					 */
					writePointNum(currentCapturePointNum);
					currentCapturePointNum++;
					writeXYZ(location, "no", "    ");
					writeOther("R: ", args[0], "    ");
					writeOther("Name: ", args[1], "    ");
					writeOther("Time: ", args[2], "    ");
				}
				else if( args.length == 4 )
				{
					/**
					 *  Manually Setting the Point Number
					 */
					writePointNum(Integer.parseInt(args[3]));
					writeXYZ(location, "no", "    ");
					writeOther("R: ", args[0], "    ");
					writeOther("Name: ", args[1], "    ");
					writeOther("Time: ", args[2], "    ");
				}
				else
				{
					return false;
				}
				
				return true;  // Command successful
			}
			
			if (cmd.getName().equalsIgnoreCase("pointother")) {
				
				location = ((Player) sender).getLocation();
				
				/**
				 *  Add a point with a different heading name (other than Point#).
				 */
				if( args.length == 1 )
				{
					/**
					 *  Manually Setting the Point Number
					 */
					if( args[0].equalsIgnoreCase("lobbyspawn") )
					{
						writePointHeader("LobbySpawn");
						writeXYZ(location, "no", "  ");
					}
					else if( args[0].equalsIgnoreCase("spawn") )
					{
						writePointHeader("  Spawn");
						writeXYZ(location, "yes", "    ");
					}
					else if( args[0].equalsIgnoreCase("penalty") )
					{
						writePointHeader("  Penalty");
						writeXYZ(location, "no", "    ");
					}
					else
					{
						writePointHeader(args[0]);
						writeXYZ(location, "yes", "  ");
					}	
				}
				else
				{
					return false;
				}
				
				return true;  // Command successful
			}
			
			if (cmd.getName().equalsIgnoreCase("pointnewsection")) {
				/**
				 *  Add new section spacing.
				 */
				newSection();
				
				if( args.length == 1)
					writeContent("-----" + args[0] + "-----\n\n");
				
				return true;  // Command successful
			}
			
			if (cmd.getName().equalsIgnoreCase("pointsetnumber")) {

				/**
				 *  Manually set the current point number, PVP or Capture.
				 */
				if( args.length == 2 )
				{
					if( args[0].equals("pvp"))
					{
						currentPVPPointNum = Integer.parseInt(args[1]);
					}
					else if( args[0].equals("capture"))
					{
						currentCapturePointNum = Integer.parseInt(args[1]);
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
				
				return true;  // Command successful
			}
			
			if (cmd.getName().equalsIgnoreCase("pointstats")) {
				/**
				 *  Show stats about currently running plugin.
				 */
				String content = "Current PVP Point: " + currentPVPPointNum + "\nCurrent Capture Point: " + currentCapturePointNum;
				Bukkit.broadcastMessage(content);
				
				return true;  // Command successful
			}
		}

		return false;  // Command fail
	}

	private void writePointNum(int pointNum) {
		/**
		 *  Write a Point with the specified number.
		 */
		String content = "\n  Point" + pointNum;
		
		writeContent(content);
	}

	private void writeXYZ(Location location, String yaw, String spacing) {
		/**
		 *  Write Coordinates with optional Yaw.  Specify Spacing.
		 */
		int X = location.getBlockX();
		int Y = location.getBlockY();
		int Z = location.getBlockZ();
		
		String content = "\n" + spacing + "X: " + X + "\n" + spacing + "Y: " + Y + "\n" + spacing + "Z: " + Z;
		
		if(yaw.equals("yes"))
		{
			int yawAmt = (int) location.getYaw();
			
			content = content + "\n" + spacing + "Yaw: " + yawAmt;
		}
		
		writeContent(content);
	}
	
	private void writeOther(String item, String value, String spacing) {
		/**
		 *  Write an entry for various items.
		 */
		String content = "\n" + spacing + item + value;
		
		writeContent(content);
	}
	
	private void writePointHeader(String content) {
		/**
		 *  Write the point header.
		 */
		writeContent(content);
	}

	public void newSection() {
		/**
		 *  Add some space to indicate a new section.
		 */
		String content = "\n\n====================  New Section  ====================\n\n";
		
		writeContent(content);
	}
	
	public void writeContent(String content) {
		/**
		 *  Try to write the String content.  Also, display what is written to server.
		 */
		try {
			buffWriter.write(content);
			Bukkit.broadcastMessage(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}