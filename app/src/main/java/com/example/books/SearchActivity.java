package com.example.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final EditText mTitle = findViewById(R.id.etTitle);
        final EditText mAuthor = findViewById(R.id.etAuthor);
        final EditText mPublisher = findViewById(R.id.etPublisher);
        final EditText mIsbn = findViewById(R.id.etIsbn);
        final Button mButton = findViewById(R.id.btnSearch);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString().trim();
                String author = mAuthor.getText().toString().trim();
                String publisher = mPublisher.getText().toString().trim();
                String isbn = mIsbn.getText().toString().trim();
                if (title.isEmpty() && author.isEmpty() && publisher.isEmpty() && isbn.isEmpty()){
                    String message = getString(R.string.search_term);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }else {
                    URL queryUrl = ApiUtils.buildUrl(title,author,publisher,isbn);

                    //shared preference

                    Context context = getApplicationContext();
                    int position = SpUtils.getPreferenceInt(context,SpUtils.POSITION);
                    if (position == 0 || position == 5){
                        position = 1;
                    }else {
                        position++;
                    }
                    String key = SpUtils.QUERY + String.valueOf(position);
                    String value = title + "," + author + "," + publisher + "," + isbn;
                    SpUtils.setPreferenceString(context,key,value);
                    SpUtils.setPreferenceInt(context,SpUtils.POSITION,position);
                    Intent intent = new Intent(getApplicationContext(),BooksActivity.class);
                    intent.putExtra("Query",queryUrl);
                    startActivity(intent);

                }

            }
        });


    }
}
