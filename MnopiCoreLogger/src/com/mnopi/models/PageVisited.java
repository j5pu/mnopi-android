package com.mnopi.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PageVisited implements Parcelable {

	private String url;
	private String domain;
	private String date;
	private String hour;
	private String resource_uri;
	private ArrayList<String> categories;
	
	public PageVisited(String url, String domain, String date, String hour,
			String resource_uri, ArrayList<String> categories) {
		super();
		this.url = url;
		this.domain = domain;
		this.date = date;
		this.hour = hour;
		this.resource_uri = resource_uri;
		this.categories = categories;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getResource_uri() {
		return resource_uri;
	}

	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(domain);
        parcel.writeString(date);
        parcel.writeString(hour);
        parcel.writeString(resource_uri);
        parcel.writeStringList(categories);
    }
}
