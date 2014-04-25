package com.mnopi.mnopiapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.webkit.URLUtil;

public class PageVisitedSender {
	
	private static final String PAGE_VISITED = "page_visited";
	
	private Context context;
	
	private static final int URL_MAX_LENGTH = 200;

	private static final String DATE_CANT_BE_A_FUTURE_DATE = "Date can't be a future date";
	private static final String URL_MUST_BE_A_CORRECT_URL = "Url must be a correct url";
	private static final String DATE_NOT_SPECIFIED = "Date not specified";
	private static final String URL_NOT_SPECIFIED = "Url not specified";
	public static final String URL_MAX_LENGTH_ERROR = "Search query must be less" +
			" than " + (URL_MAX_LENGTH + 1) + " characters";

	
	public PageVisitedSender(Context ctx) {
		this.context = ctx;
	}
	
	private void validateParameters(String url, String htmlCode, Calendar date) 
			throws PageValidationException {
		
		/* Parameters exist */ 
		if (url == null || url.equals("")){
			throw new PageValidationException(URL_NOT_SPECIFIED);
		}
		
		if (date == null) {
			throw new PageValidationException(DATE_NOT_SPECIFIED);
		}
		
		/* Length validation */
		if (url.length() > URL_MAX_LENGTH){
			throw new PageValidationException(URL_MAX_LENGTH_ERROR);
		}

        /* Url must be an url */
        if (!URLUtil.isValidUrl(url) ||
                !(URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url))) {
            throw new PageValidationException(URL_MUST_BE_A_CORRECT_URL);
        }

		/* Date can't be a future date */
		if (date.compareTo(Calendar.getInstance()) > 0) {
			throw new PageValidationException(DATE_CANT_BE_A_FUTURE_DATE);
		}		
		
	}
	
	/**
	 * Sends a page visited to the server. Html code can be empty or null if no html
	 *  was saved with the page
	 *  
	 * @param url
	 * @param htmlCode
	 * @param date
	 * @throws PageValidationException
	 */
	public void send(String url, String htmlCode, Calendar date) 
			throws PageValidationException {
		
		if (htmlCode == null){
			htmlCode = "";
		}
		
		validateParameters(url, htmlCode, date);
		
		String formattedDate = Utils.getFormattedDate(date);
		
		PageVisited pageObject = new PageVisited(url, formattedDate, htmlCode);
		new PageVisitedSenderTask().execute(pageObject);
	}
	
	/**
	 * Uses now as date
	 * 
	 * @param url
	 * @param htmlCode
	 */
	public void send(String url, String htmlCode)
			throws PageValidationException {
		
		send(url, htmlCode, Calendar.getInstance()); 
		
	}
	
	/**
	 * Html not specified
	 * 
	 * @param url
	 * @param date
	 */
	public void send(String url, Calendar date) throws PageValidationException {

		send(url, "", Calendar.getInstance());
		
	}
	
	/**
	 * Uses now as date. Html not specified
	 * 
	 * @param url
	 */
	public void send(String url) throws PageValidationException {
		
		send(url, "", Calendar.getInstance());
		
	}
	
	private class PageVisitedSenderTask extends AsyncTask<PageVisited, Void, Void>{
		
		@Override
		protected Void doInBackground(PageVisited... pagesVisited){
			
			//TODO: convertir a explicit intent o a content provider
			Intent pageVisitedIntent = new Intent("com.mnopi.services.DataCollectorService");
			
			pageVisitedIntent.putExtra("url", pagesVisited[0].url);
			pageVisitedIntent.putExtra("date", pagesVisited[0].date);
			pageVisitedIntent.putExtra("html_code", pagesVisited[0].htmlCode);
			pageVisitedIntent.putExtra("handler_key", PAGE_VISITED);
			
			context.startService(pageVisitedIntent);
			
			return null;
			
		}
	}
}
