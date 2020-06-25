package com.example.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class BooksActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
TextView mTextView;
RecyclerView rvBooks;
private ProgressBar mLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvBooks = findViewById(R.id.rv_books);
        LinearLayoutManager bookLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
       rvBooks.setLayoutManager(bookLayoutManager);
        mLoading = findViewById(R.id.pb_loading);

        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");
        URL bookUrl;

        try {
            if (query == null || query.isEmpty()) {
             bookUrl = ApiUtils.buildUrl("cooking");

            }else {
              bookUrl = new URL(query);
            }
            new BookQueryTask().execute(bookUrl);
        }catch (Exception e){
            Log.e("error", e.getMessage());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        ArrayList<String> recentList = SpUtils.getQuerylist(getApplicationContext());
        int itemNum = recentList.size();
        MenuItem menuItem;
        for (int i = 0;i<itemNum;i++){
            menuItem = menu.add(Menu.NONE,i,Menu.NONE,recentList.get(i));

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.advance_search:
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                return true;
                default:
                    int position =  item.getItemId() + 1;
                    String preferenceName = SpUtils.QUERY + String.valueOf(position);
                    String query = SpUtils.getPreferenceString(getApplicationContext(),preferenceName);
                    String[] prefParams = query.split("\\,");
                    String[] queryParam = new String[4];
                    for(int i= 0;i<prefParams.length;i++){
                        queryParam[i] = prefParams[i];
                    }
            URL bookUrl =ApiUtils.buildUrl(
                    (queryParam[0]==null)?"":queryParam[0],
                    (queryParam[1]==null)?"":queryParam[1],
                    (queryParam[2]==null)?"":queryParam[2],
                    (queryParam[3]==null)?"":queryParam[3]
            );
                    new BookQueryTask().execute(bookUrl);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            URL  bookUrl = ApiUtils.buildUrl(query);
            new BookQueryTask().execute(bookUrl);

        }catch (Exception e){
            Log.e("error",e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public class BookQueryTask extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl =urls[0];
            String result = null;
            try {
                result = ApiUtils.getJson(searchUrl);
            }catch (Exception e){
                Log.e("error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView textError = findViewById(R.id.tvError);
            mLoading.setVisibility(View.INVISIBLE);
            if (result == null){
                textError.setVisibility(View.VISIBLE);
                rvBooks.setVisibility(View.INVISIBLE);
            }else {
                textError.setVisibility(View.INVISIBLE);
               rvBooks.setVisibility(View.VISIBLE);
                ArrayList<Book> books = ApiUtils.getBooksFromJson(result);
                String resultString = "";
                BookAdapter adapter = new BookAdapter(books);
                rvBooks.setAdapter(adapter);
            }



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoading.setVisibility(View.VISIBLE);
        }
    }


}
