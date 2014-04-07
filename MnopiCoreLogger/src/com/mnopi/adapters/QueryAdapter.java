package com.mnopi.adapters;

import java.util.ArrayList;


import com.mnopi.mnopi.R;
import com.mnopi.models.Query;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class QueryAdapter extends ArrayAdapter<Query>{
	
	private ArrayList<Query> queries;
	private int my_layout;
	private Context context;
	
	public QueryAdapter(Context context, int resource, ArrayList<Query> queries){
		super (context, resource, queries);
		this.queries = queries;
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
    	 

    	Query query = queries.get(position);
    	
    	TextView txt_query = (TextView) myView.findViewById(R.id.txt_query);
    	TextView txt_date = (TextView) myView.findViewById(R.id.txt_date);

    	
    	txt_query.setText(query.getQuery());
    	txt_date.setText(query.getDate());
    	
    	return myView;
    	
    }

}
