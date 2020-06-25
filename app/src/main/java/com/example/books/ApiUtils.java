package com.example.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtils {
    private ApiUtils(){}

    public static final String BASE_API_URL = "https://www.googleapis.com/books/v1/volumes";
   public static String Query_Parameter_Key = "q";
   public static final  String KEY = "key";
   public static final String API_KEY = "AIzaSyCjQ10WTm2R3KhHZJG2UWqXHsahsv2wjqM";
   public static final String TITLE = "intitle:";
   public static final  String AUTHOR = "inauthor:";
   public static final String PUBLISHER = "inpublisher";
   public static final String ISBN = "inisbn:";

    public static URL buildUrl(String title){

       URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(Query_Parameter_Key,title).build();
        try{
            url = new URL(uri.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrl(String title,String author,String publisher,String isbn){
        URL url = null;
        StringBuilder builder = new StringBuilder();
        if (!title.isEmpty()) builder.append(TITLE + title + "+");
        if (!author.isEmpty()) builder.append(AUTHOR + author + "+");
        if (!publisher.isEmpty()) builder.append(PUBLISHER+ publisher + "+");
        if (!isbn.isEmpty()) builder.append(ISBN + isbn + "+");
        builder.setLength(builder.length() -1);
        String query = builder.toString();
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(Query_Parameter_Key,query)
                .appendQueryParameter(KEY,API_KEY).build();

        try{
            url = new URL(uri.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getJson(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
            if (hasData){
                return scanner.next();
            }else {
                return null;
            }
        }catch (Exception e){
            Log.d("Error",e.toString());
            return null;
        }finally {
            connection.disconnect();
        }
    }

    public static ArrayList<Book> getBooksFromJson(String json){
        final String ID ="id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String VOLUME_INFO = "volumeInfo";
        final String ITEMS = "items";
        final String DESCRIPTION = "description";
        final String IMAGELINKS = "imageLinks";
        final String THUMBNAIL = "thumbnail";

        ArrayList<Book> books = new ArrayList<Book>();

        try {
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();
            for (int i =0; i < numberOfBooks; i++){
                JSONObject bookJson = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJson = bookJson.getJSONObject(VOLUME_INFO);
                JSONObject imagelinksJson = null;
                if (volumeInfoJson.has(IMAGELINKS)){
                    imagelinksJson = volumeInfoJson.getJSONObject(IMAGELINKS);
                }

                int authorsNum ;
                try {
                    authorsNum= volumeInfoJson.getJSONArray(AUTHORS).length();
                }catch (Exception e){
                    authorsNum = 0;
                }

                String[] authors = new String[authorsNum];
                for (int j = 0;j<authorsNum;j++){
                    authors[j]= volumeInfoJson.getJSONArray(AUTHORS).get(j).toString();
                }
                Book book = new Book(
                        bookJson.getString(ID),
                        volumeInfoJson.getString(TITLE),
                        (volumeInfoJson.isNull(SUBTITLE)? "":volumeInfoJson.getString(SUBTITLE)),authors,
                        volumeInfoJson.isNull(PUBLISHER) ? "": volumeInfoJson.getString(PUBLISHER),
                        volumeInfoJson.isNull(PUBLISHED_DATE) ? "":volumeInfoJson.getString(PUBLISHED_DATE),
                       volumeInfoJson.isNull(DESCRIPTION) ? "": volumeInfoJson.getString(DESCRIPTION),
                        (imagelinksJson == null) ? "": imagelinksJson.getString(THUMBNAIL));
                books.add(book);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return books;

    }
}
