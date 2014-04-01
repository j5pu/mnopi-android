package com.mnopi.mnopiapi;

/**
 * Page visited struct
 * @author alainez
 *
 */
public class PageVisited {
	
	public String url;
	public String date;
	public String htmlCode;
	
	public PageVisited(String url, String date, String htmlCode) {
		this.url = url;
		this.date = date;
		this.htmlCode = htmlCode;
	}
	
	public PageVisited(String url, String date) {
		this.url = url;
		this.date = date;
		this.htmlCode = "";
	}
	

}
