package com.mnopi.mnopi;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mnopi.adapters.QueryAdapter;
import com.mnopi.data.DataProvider;
import com.mnopi.dialogs.QueryDialog;
import com.mnopi.models.Query;

import java.util.ArrayList;

/**
 * Created by Josko on 13/05/2014.
 */
public class ViewQueriesDeviceActivity extends Activity{


    private ArrayList<Query> queries;
    private ListView listQueries;
    private QueryAdapter qAdapter;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewqueries);

        queries = new ArrayList<Query>();
        listQueries = (ListView) findViewById(R.id.listQueries);
        qAdapter = new QueryAdapter(this, R.layout.query_item, queries);
        listQueries.setAdapter(qAdapter);
        mContext = this;

        /* get data from content provider
			 */
        String[] projection = new String[]{
                DataProvider.WebSearch._ID,
                DataProvider.WebSearch.COL_QUERY,
                DataProvider.WebSearch.COL_URL,
                DataProvider.WebSearch.COL_DATE
        };
        Uri webSearchUri = DataProvider.WEB_SEARCH_URI;
        ContentResolver cr = mContext.getContentResolver();

        Cursor cursor = cr.query(webSearchUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(DataProvider.WebSearch._ID));
                String url = cursor.getString(cursor.getColumnIndex(DataProvider.WebSearch.COL_URL));
                String query = cursor.getString(cursor.getColumnIndex(DataProvider.WebSearch.COL_QUERY));
                String date = cursor.getString(cursor.getColumnIndex(DataProvider.WebSearch.COL_DATE));
                String dateFormated = date.substring(0, 10);
                String hour = date.substring(11, 19);
                Query queryAux = new Query(id, query
                        , dateFormated, url, hour);
                queries.add(queryAux);
            }while (cursor.moveToNext());
        }
        cursor.close();

        listQueries.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Query query = queries.get(position);
                QueryDialog qDialog = new QueryDialog(mContext, query);
                qDialog.setTitle(query.getQuery());
                qDialog.show();
            }

        });
    }
}
