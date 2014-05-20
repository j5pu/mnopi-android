package com.mnopi.mnopi;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.mnopi.data.adapters.QueryAdapter;
import com.mnopi.data.DataProvider;
import com.mnopi.data.dialogs.QueryDialog;
import com.mnopi.data.models.Query;

import java.util.ArrayList;

/**
 * Created by Josko on 13/05/2014.
 */
public class ViewQueriesDeviceActivity extends Activity{


    private ArrayList<Query> queries;
    private ListView listQueries;
    private QueryAdapter qAdapter;
    private Context mContext;
    private Button btnRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewqueriesdevice);

        showData();

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

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showData();
            }
            });
    }

    public void showData(){
        queries = new ArrayList<Query>();
        listQueries = (ListView) findViewById(R.id.listQueries);
        qAdapter = new QueryAdapter(this, R.layout.query_item, queries);
        listQueries.setAdapter(qAdapter);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
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
    }
}
