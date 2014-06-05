package com.mnopi.data.adapters;

import java.util.ArrayList;


import com.mnopi.mnopi.R;
import com.mnopi.data.models.Query;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class QueryAdapter extends ArrayAdapter<Query>{
	
	private ArrayList<Query> queries;
	private int layout;
	private Context context;
	
	public QueryAdapter(Context context, int resource, ArrayList<Query> queries){
		super (context, resource, queries);
		this.queries = queries;
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
    	 

    	Query query = queries.get(position);
    	
    	TextView txtQuery = (TextView) myView.findViewById(R.id.txt_query);
    	TextView txtDate = (TextView) myView.findViewById(R.id.txt_date);

    	
    	txtQuery.setText(query.getQuery());
    	txtDate.setText(query.getDate());
    	
    	return myView;
    	
    }

}
