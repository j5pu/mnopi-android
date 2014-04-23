package com.mnopi.mnopiapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class SearchSender {

	private final String WEB_SEARCH_HANDLER = "web_search";

	private static final short SEARCH_QUERY_MAX_LENGTH = 300;
	private static final short SEARCH_RESULTS_MAX_LENGTH = 500;

	public static final String DATE_CANT_BE_A_FUTURE_DATE = "Date can't be a future date";
	public static final String SEARCH_RESULT_MUST_BE_AN_URL = "Search results must be an url";
	public static final String DATE_NOT_SPECIFIED = "Date not specified";
	public static final String SEARCH_RESULTS_NOT_SPECIFIED = "Search results not specified";
	public static final String SEARCH_QUERY_NOT_SPECIFIED = "Search query not specified";
	public static final String SEARCH_RESULTS_MAX_LENGTH_ERROR = "Search results must be less " +
			"than " + (SEARCH_RESULTS_MAX_LENGTH + 1) + " characters";
	public static final String SEARCH_QUERY_MAX_LENGTH_ERROR = "Search query must be less " +
			"than " + (SEARCH_QUERY_MAX_LENGTH + 1) + " characters";
	
	private Context context;
	
	//TODO: We have to think if this will be a singleton in order for it to be easier
	// to call in big Android applications like Firefox
	public SearchSender(Context ctx) {
		this.context = ctx;
	}

	
	private void validateParameters(String searchQuery, String searchResults, Calendar date) 
			throws SearchValidationException {
		
		/* Parameters exist */
		if (searchQuery == null || searchQuery.equals("")){
			throw new SearchValidationException(SEARCH_QUERY_NOT_SPECIFIED);
		}
		if (searchQuery == null || searchResults.equals("")){
			throw new SearchValidationException(SEARCH_RESULTS_NOT_SPECIFIED);
		}
		if (date == null) {
			throw new SearchValidationException(DATE_NOT_SPECIFIED);
		}
		
		/* Length validation */
		if (searchQuery.length() > SEARCH_QUERY_MAX_LENGTH) {
			throw new SearchValidationException(SEARCH_QUERY_MAX_LENGTH_ERROR);
		}
		
		if (searchResults.length() > SEARCH_RESULTS_MAX_LENGTH) {
			throw new SearchValidationException(SEARCH_RESULTS_MAX_LENGTH_ERROR);
		}
		
		/* Search results must be an URL*/
		try {
			new URL(searchResults);
		} catch (MalformedURLException e) {
			throw new SearchValidationException(SEARCH_RESULT_MUST_BE_AN_URL);
		}
		
		/* Date can't be a future date */
		if (date.compareTo(Calendar.getInstance()) > 0) {
			throw new SearchValidationException(DATE_CANT_BE_A_FUTURE_DATE);
		}

	}
	
	/**
	 * Sends a search query to the server 
	 * 
	 * @param searchQuery
	 * @param searchResults url with the results of the query
	 * @param date
	 * @throws SearchValidationException
	 */
	public void send(String searchQuery, String searchResults, Calendar date) 
			throws SearchValidationException {
		
		validateParameters(searchQuery, searchResults, date);
		
		String formattedDate = Utils.getFormattedDate(date);
		
		QuerySearch searchObject = new QuerySearch(searchQuery, searchResults, formattedDate);
		new SearchSenderTask().execute(searchObject);

	}
	
	/**
	 * Send a search query to the server
	 * 
	 * @param searchQuery
	 * @param searchResults url with the results of the query
	 * @throws SearchValidationException
	 */
	public void send(String searchQuery, String searchResults) 
			throws SearchValidationException {

		Calendar rightNow = Calendar.getInstance();
		send(searchQuery, searchResults, rightNow);
		
	}
	
	private class SearchSenderTask extends AsyncTask<QuerySearch, Void, Void>{
		
		@Override
		protected Void doInBackground(QuerySearch... searches){

			//TODO: Convertir a explicit intent
			Intent searchIntent = new Intent("com.mnopi.services.DataCollectorService");
			
			searchIntent.putExtra("search_query", searches[0].searchQuery);
			searchIntent.putExtra("search_results", searches[0].searchResults);
			searchIntent.putExtra("date", searches[0].date);
			searchIntent.putExtra("handler_key", WEB_SEARCH_HANDLER);

			try {
				context.startService(searchIntent);
			} catch (SecurityException ex){
			}
			
			return null;
			
		}
	}
}
