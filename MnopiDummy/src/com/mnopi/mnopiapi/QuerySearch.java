package com.mnopi.mnopiapi;

/**
 * Query search struct
 * @author alainez
 *
 */
public class QuerySearch {
	
	public String searchQuery;
	public String searchResults;
	public String date;
	
	public QuerySearch(String searchQuery, String searchResults, String date){
		this.searchQuery = searchQuery;
		this.searchResults = searchResults;
		this.date = date;
	}

}
