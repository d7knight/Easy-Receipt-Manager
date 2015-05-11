package com.example.easyreceiptmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity<Activity_Main> extends Activity {
   	public static String storageDir = Environment.getExternalStorageDirectory().getPath()
   			                         +"/RecieptManager/";
	
	 public static ArrayAdapter<String> ad1;
	 public static ArrayAdapter<String> ad2;
	 
	 private Context AppContext;
	 private Boolean typesempty;
	 private Boolean storesempty;
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    AppContext=this;
		Initializead1();
		Initializead2();
		
	  }
	 
	  // add items into spinner dynamically
	  public void Initializead1() {
	 
		List<String> list = new ArrayList<String>();
		File folder=new File(storageDir);
		if (!folder.exists())folder.mkdir();
		File previoustypes=new File(storageDir+"types.txt");
		if (previoustypes.exists()){
			Scanner myscanner = null;
		    try {
		        myscanner = new Scanner(previoustypes);
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();  
		    }
		    while (myscanner.hasNextLine()) {
		    	list.add(myscanner.nextLine());
		    }
		    myscanner.close();
		    typesempty=false;
		}
		else{
		list.add("No Types defined");
		typesempty=true;
		}
		ad1 = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_dropdown_item, list);
		ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
       	  
	  
	  }
	  public void Initializead2() {
			 
			List<String> list = new ArrayList<String>();
			File folder=new File(storageDir);
			if (!folder.exists())folder.mkdir();
			File previousstores=new File(storageDir+"stores.txt");
			if (previousstores.exists()){
				Scanner myscanner = null;
			    try {
			        myscanner = new Scanner(previousstores);
			    } catch (FileNotFoundException e) {
			        e.printStackTrace();  
			    }
			    while (myscanner.hasNextLine()) {
			    	list.add(myscanner.nextLine());
			    }
			    myscanner.close();
			    storesempty=false;
			}
			else{
			list.add("No Stores defined");
			storesempty=true;
			}
			ad2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, list);
			ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       	  
		  
		  }
	  public void add1(View v) {
		  
		    final Dialog dialog = new Dialog(AppContext);
			dialog.setContentView(R.layout.addmenu);
			dialog.setTitle("Add Type");
			

			// set the custom dialog components - text, image and button
			
			Button submit = (Button) dialog.findViewById(R.id.submit);
		
			
			submit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					EditText type = (EditText) dialog.findViewById(R.id.name);
					
					if(typesempty)ad1.clear();
					ad1.add(type.getText().toString());
					Toast.makeText(AppContext,
							"Added " + type.getText().toString(),Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					typesempty=false;
					File previoustypes=new File(storageDir+"types.txt");
				
					PrintWriter out = null;
					try {
						if(!previoustypes.exists())previoustypes.createNewFile();
					    out = new PrintWriter(new BufferedWriter(new FileWriter(previoustypes, true)));
					    out.println(type.getText().toString());
					    out.close();
					}catch (IOException e) {
					   
					}
					
					
							
					
					
				}
			}); 
			
			dialog.show();
			
		  }
	  public void remove1(View v) {
		  
		    final Dialog dialog = new Dialog(AppContext);
			dialog.setContentView(R.layout.removemenu);
			dialog.setTitle("Remove Type");
			

			// set the custom dialog components - text, image and button
			Spinner types = (Spinner) dialog.findViewById(R.id.spinner);
			Button remove = (Button) dialog.findViewById(R.id.remove);
		
			types.setAdapter(ad1);
			types.setOnItemSelectedListener(new CustomOnItemSelectedListener());
			
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Spinner types = (Spinner) dialog.findViewById(R.id.spinner);
					String toremove=(String) types.getSelectedItem();
					
				
					try {
						String oldfile = readfromFile(storageDir+"types.txt");
						String newfile=oldfile.replaceFirst(toremove+"\n", "");
						File previoustypes=new File(storageDir+"types.txt");					
						FileWriter out = new FileWriter(previoustypes);
						out.write(newfile);
						out.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					
				   ad1.remove(toremove);
					Toast.makeText(AppContext,
							"Removed " + toremove,Toast.LENGTH_SHORT).show();
				  dialog.dismiss();
					
							
					
					
				}
			}); 
			
			dialog.show();
			
		  }
	  public void add2(View v) {
		  
		    final Dialog dialog = new Dialog(AppContext);
			dialog.setContentView(R.layout.addmenu);
			dialog.setTitle("Add Store");
			
			
			Button submit = (Button) dialog.findViewById(R.id.submit);
		
			
			submit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					EditText store = (EditText) dialog.findViewById(R.id.name);
					if(storesempty)ad2.clear();
					ad2.add(store.getText().toString());
					Toast.makeText(AppContext,
							"Added " + store.getText().toString(),Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					storesempty=false;
					File previoustypes=new File(storageDir+"stores.txt");
				
					PrintWriter out = null;
					try {
						if(!previoustypes.exists())previoustypes.createNewFile();
					    out = new PrintWriter(new BufferedWriter(new FileWriter(previoustypes, true)));
					    out.println(store.getText().toString());
					    out.close();
					}catch (IOException e) {
					   
					}
					
					
							
					
					
				}
			}); 
			
			dialog.show();
			
		  }
	  public void remove2(View v) {
		  
		    final Dialog dialog = new Dialog(AppContext);
			dialog.setContentView(R.layout.removemenu);
			dialog.setTitle("Remove Store");
			

			// set the custom dialog components - text, image and button
			Spinner stores = (Spinner) dialog.findViewById(R.id.spinner);
			Button remove = (Button) dialog.findViewById(R.id.remove);
		
			stores.setAdapter(ad2);
			stores.setOnItemSelectedListener(new CustomOnItemSelectedListener());
			
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Spinner stores = (Spinner) dialog.findViewById(R.id.spinner);
					String toremove=(String) stores.getSelectedItem();
					
				
					try {
						String oldfile = readfromFile(storageDir+"stores.txt");
						String newfile=oldfile.replaceFirst(toremove+"\n", "");
						File previoustypes=new File(storageDir+"stores.txt");					
						FileWriter out = new FileWriter(previoustypes);
						out.write(newfile);
						out.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				
					
				   ad2.remove(toremove);
					Toast.makeText(AppContext,
							"Removed " + toremove,Toast.LENGTH_SHORT).show();
				  dialog.dismiss();
					
							
					
					
				}
			}); 
			
			dialog.show();
			
		  }
	  public void recieptmaker(View v) {
		  
		  Intent i = new Intent(this, Recieptmaker.class);
		  startActivity(i);
		  
		  
	  }
      public void viewer(View v) {
		  
		  Intent i = new Intent(this, Viewer.class);
		  startActivity(i);
		  
		  
	  }
		public static String readfromFile(String path) throws IOException {
			  FileInputStream stream = new FileInputStream(new File(path));
			  try {
			    FileChannel fc = stream.getChannel();
			    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			    return Charset.defaultCharset().decode(bb).toString();
			  }
			  finally {
			    stream.close();
			  }
			}
	  
	  
}
	  
	
	
	  

	