package com.mnopi.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mnopi.mnopi.R;
import com.mnopi.models.PageVisited;

public class PageAdapter extends ArrayAdapter<PageVisited>{
	
	private ArrayList<PageVisited> pages;
	private int my_layout;
	private Context context;
	
	public PageAdapter(Context context, int resource, ArrayList<PageVisited> pages){
		super (context, resource, pages);
		this.pages = pages;
		this.my_layout = resource;
		this.context = context;
	}
	
	/**
     * getView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View myView ;
    	
    	 LayoutInflater inflater = ((Activity)context).getLayoutInflater();
    	 myView = inflater.inflate(my_layout, parent, false);
    	 

    	PageVisited pageVisited = pages.get(position);
    	
    	TextView txt_domain = (TextView) myView.findViewById(R.id.txt_domain);
    	TextView txt_date = (TextView) myView.findViewById(R.id.txt_date);

    	
    	txt_domain.setText(pageVisited.getDomain());
    	txt_date.setText(pageVisited.getDate());
    	
    	return myView;
    	
    } 

}
