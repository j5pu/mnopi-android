package com.mnopi.data.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mnopi.mnopi.R;
import com.mnopi.data.models.PageVisited;

public class PageAdapter extends ArrayAdapter<PageVisited>{
	
	private ArrayList<PageVisited> pages;
	private int layout;
	private Context context;
	
	public PageAdapter(Context context, int resource, ArrayList<PageVisited> pages){
		super (context, resource, pages);
		this.pages = pages;
		this.layout = resource;
		this.context = context;
	}
	
	/**
     * getView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View myView ;
    	
    	 LayoutInflater inflater = ((Activity)context).getLayoutInflater();
    	 myView = inflater.inflate(layout, parent, false);
    	 

    	PageVisited pageVisited = pages.get(position);
    	
    	TextView txtDomain = (TextView) myView.findViewById(R.id.txt_domain);
    	TextView txtDate = (TextView) myView.findViewById(R.id.txt_date);

    	
    	txtDomain.setText(pageVisited.getDomain());
    	txtDate.setText(pageVisited.getDate());
    	
    	return myView;
    	
    } 

}
