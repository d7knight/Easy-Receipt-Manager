package com.example.easyreceiptmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Recieptmaker extends Activity {
	private EditText DisplayDate;
	private EditText amount;
	private Context AppContext;
	private int year;
	private int month;
	private int day;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creator);
		AppContext = this;
		setCurrentDateOnView();
		addListenerOnEdittext();
		addwatcheronamount();
		initializespinners();

	}
	public void addwatcheronamount(){
		
		amount = (EditText) findViewById(R.id.amount);
		
		amount.addTextChangedListener(tw);
		
		
		}
	
	
     TextWatcher tw=new TextWatcher() {
        
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        	
        }

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        	
        }

        public void afterTextChanged(Editable s)
        {   
        	if(!s.toString().startsWith("$")) {
        		amount.removeTextChangedListener(tw);
            	amount.setText("$"+amount.getText().toString());
            	if(s.length()==1)amount.setSelection(2);
            	else if(s.length()==0)amount.setSelection(1);
            	amount.addTextChangedListener(tw);
            	
            }
        }
        };




	private void initializespinners() {
		 
		Spinner types = (Spinner) findViewById(R.id.spinner1);
		Spinner stores = (Spinner) findViewById(R.id.spinner2);
		types.setAdapter(MainActivity.ad1);
		stores.setAdapter(MainActivity.ad2);
		
	}

	// display current date
	public void setCurrentDateOnView() {

		DisplayDate = (EditText) findViewById(R.id.etdate);

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		// set current date into textview
		DisplayDate.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(month + 1).append("-").append(day).append("-")
				.append(year).append(" "));

	}
	public void back(View v){
        
        finish();
    }
	public void addListenerOnEdittext() {

		DisplayDate = (EditText) findViewById(R.id.etdate);

		DisplayDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(AppContext);
				dialog.setContentView(R.layout.datep);
				dialog.setTitle("Choose Date");
				Button setbtn = (Button) dialog.findViewById(R.id.done);
				setbtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						DatePicker datep = (DatePicker) dialog
								.findViewById(R.id.datep);
                        month=datep.getMonth();
                        year=datep.getYear();
                        day=datep.getDayOfMonth();
                        DisplayDate.setText(new StringBuilder()
        				// Month is 0 based, just add 1
        				.append(month + 1).append("-").append(day).append("-")
        				.append(year).append(" "));
						dialog.dismiss();
						
					}

				});
				dialog.show();

			}

		});

	}
	private Double sumreceipts(String r){
		
		
		Matcher m = Pattern.compile(
                Pattern.quote("<td style=padding:3px;>$")
                + "(.*?)"
                + Pattern.quote("</td>")
       ).matcher(r); 
		String match="";
		Double total=(double) 0;
		
		while(m.find()){
		
			match = m.group(1);
			total+=Double.parseDouble(match);
			
		}
		total-=Double.parseDouble(match);
		
		
		
		
		return total;
		
	}
	
	public void savedata(View v){
		
		String d=DisplayDate.getText().toString();
		String t=(String) ((Spinner) findViewById(R.id.spinner1)).getSelectedItem();
		String s=(String) ((Spinner) findViewById(R.id.spinner2)).getSelectedItem();
		String a=((EditText) findViewById(R.id.amount)).getText().toString();
		if(a.equals(""))a="$0";
		if(a.equals("$"))a="$0";
		
		File fldr=new File(MainActivity.storageDir);
		if(!fldr.exists())fldr.mkdirs();
		File datab=new File(MainActivity.storageDir+"datab.htm");
        
			try {
				if(!datab.exists())datab.createNewFile();
				String oldfile = MainActivity.readfromFile(MainActivity.storageDir+"datab.htm");				
				FileWriter out = new FileWriter(datab);
				SimpleDateFormat dfDate  = new SimpleDateFormat("EEE, MMM d yyyy hh:mm aaa");
	              String data="";
	              Calendar cal = Calendar.getInstance(); 
	              data=dfDate.format(cal.getTime());
			      out.write("<html><body><p>Your Receipt Database " +
                          "<br> As of " + data + "<br></p>" +
                          "<table border=1 style=background-color:white;" +
                          "border:1px:black;width:80%;border-collapse:collapse;>" );
			      Double aval=Double.parseDouble(a.replace("$",""));
			      Double grandtotal=aval;
			      NumberFormat nf = new DecimalFormat("##.###");
			      
			      if (!oldfile.equals("")){
			    	 String temp1="collapse;>";
			       int b=oldfile.indexOf(temp1)+temp1.length();
			   	   oldfile=oldfile.substring(b);
			   	   oldfile=oldfile.trim();
			   	 String marker= "<tr style=background-color:orange;color:black;><th style=padding:3px; colspan=3>";
			     String[] types=oldfile.split(marker);
			     int tlen=types.length;
			     temp1="<td style=padding:3px; colspan=2>" + "Grand Total";
			     b=types[tlen-1].indexOf(temp1);
			     types[tlen-1]=types[tlen-1].substring(0, b);
			     
			     String temp2="";
			     int e=0;
			     
			     
			     
				for(int i=1; i<tlen; i++){
				   	   b=0;
					   temp1=" Receipts";
				   	   e=types[i].indexOf(temp1);
				       temp2=types[i].substring(b,e);
				      
				       Double total;
				       if (temp2.compareTo(t)>0){
							
							out.write(
									marker + t + " Receipts" +
							            "</th>"+
							            "</tr>");
							
							out.write(
						                
						   		  	    "<tr style=background-color:orange;color:black;>" +
							            "<th style=padding:3px;>Date</th>"+
						                "<th style=padding:3px;>Store</th>"+		                      
						                "<th style=padding:3px;>Amount</th>"+
							              "</tr>");
							out.write("<tr><td style=padding:3px;>"+d+"</td>");
						    out.write("<td style=padding:3px;>"+s+"</td>");
						    out.write("<td style=padding:3px;>"+a+"</td></tr>");
							out.write(
					                
					   		  	    "<tr><td style=padding:3px; colspan=2>" + "Total" +
						            "</td>"+
						            "<td style=padding:3px;>" + a +
						            "</td></tr>");
							for (int j=i;j<tlen;j++){
								grandtotal+=sumreceipts(types[j]);
		                        out.write(marker+types[j]);
								
							}
						     break;
							}
							else if (temp2.compareTo(t)==0){
								temp1="<td style=padding:3px; colspan=2>" + "Total";
								b=types[i].indexOf(temp1);
								temp1=types[i].substring(0,b);
								
		                        
		                    	temp1+="<tr><td style=padding:3px;>"+d+"</td>"+
		                    	       "<td style=padding:3px;>"+s+"</td>"+
		                    		   "<td style=padding:3px;>"+a+"</td></tr>";
		    				    
		    				    total=sumreceipts(types[i])+aval;
		    			       
		                        temp1+=
						   		  	    "<td style=padding:3px; colspan=2>" + "Total" +
							            "</td>"+
							            "<td style=padding:3px;>$" + nf.format(total) +
							            "</td></tr>";
		                        
		                        out.write(marker+temp1);
		                        grandtotal+=sumreceipts(types[i]);
		                        for (int j=i+1;j<tlen;j++){
		                        	grandtotal+=sumreceipts(types[j]);
			                        out.write(marker+types[j]);
		                        }
		                        break;
								
							}
		                    else if (temp2.compareTo(t)<0){
		                        out.write(marker+types[i]);
		                        grandtotal+=sumreceipts(types[i]);
		                        if(i==tlen-1){

		         			       out.write(
		         				                
		         				   		  	    "<tr style=background-color:orange;color:black;>" +
		         					            "<th style=padding:3px; colspan=3>" + t + " Receipts" +
		         					            "</th>"+
		         					              "</tr>");
		         					
		         					out.write(
		         				                
		         				   		  	    "<tr style=background-color:orange;color:black;>" +
		         					            "<th style=padding:3px;>Date</th>"+
		         				                "<th style=padding:3px;>Store</th>"+		                      
		         				                "<th style=padding:3px;>Amount</th>"+
		         					              "</tr>");
		         					out.write("<tr><td style=padding:3px;>"+d+"</td>");
		         				    out.write("<td style=padding:3px;>"+s+"</td>");
		         				    out.write("<td style=padding:3px;>"+a+"</td></tr>");
		         					out.write(
		         			                
		         			   		  	    "<tr><td style=padding:3px; colspan=2>" + "Total" +
		         				            "</td>"+
		         				            "<td style=padding:3px;>" + a +
		         				            "</td></tr>");
		                        	
		                          break;	
		                        }	
							}
								 
							
						}
					
			      }
			      else{
			       out.write(
				                
				   		  	    "<tr style=background-color:orange;color:black;>" +
					            "<th style=padding:3px; colspan=3>" + t + " Receipts" +
					            "</th>"+
					              "</tr>");
					
					out.write(
				                
				   		  	    "<tr style=background-color:orange;color:black;>" +
					            "<th style=padding:3px;>Date</th>"+
				                "<th style=padding:3px;>Store</th>"+		                      
				                "<th style=padding:3px;>Amount</th>"+
					              "</tr>");
					out.write("<tr><td style=padding:3px;>"+d+"</td>");
				    out.write("<td style=padding:3px;>"+s+"</td>");
				    out.write("<td style=padding:3px;>"+a+"</td></tr>");
					out.write(
			                
			   		  	    "<tr><td style=padding:3px; colspan=2>" + "Total" +
				            "</td>"+
				            "<td style=padding:3px;>" + a +
				            "</td></tr>");  
			    	  
			    	  
			    	  
			    	  
			      }
			     
			      
			    
				out.write("<tr><td style=padding:3px; colspan=2>" + "Grand Total" +
		            "</td>"+
		            "<td style=padding:3px;>$" + nf.format(grandtotal) +
		            "</td></tr>");	
				out.write("</html></body>");
				
				
			      
			      
			      
			      
				out.close();
				
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        
        
		
	}

}
