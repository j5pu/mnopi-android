package com.mnopi.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Query implements Parcelable{


	private String resource_uri;
	private String query;
	private String date;
	private String result;
	private String hour;

	public Query(){
		super();
	}

	public Query(String id, String query, String date, String result, String hour) {
		super();
		this.resource_uri = id;
		this.query = query;
		this.date = date;
		this.result = result;
		this.hour = hour;
	}

	public String getResource_uri() {
		return resource_uri;
	}

	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(resource_uri);
        parcel.writeString(query);
        parcel.writeString(date);
        parcel.writeString(result);
        parcel.writeString(hour);
    }
}
