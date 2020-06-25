package com.example.books;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
ArrayList<Book> books;
    public BookAdapter(ArrayList<Book> books){
    this.books = books;
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itewView = LayoutInflater.from(context).inflate(R.layout.book_list_item,parent,false);
        return new BookViewHolder(itewView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle,
        tvAuthor,tvPublisher,tvDate;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthors);
            tvPublisher = itemView.findViewById(R.id.publisher);
            tvDate = itemView.findViewById(R.id.tvPublisherDate);
            itemView.setOnClickListener(this);
        }
        public void bind (Book book){
            tvTitle.setText(book.title);
            String authors = "";
            tvAuthor.setText(authors);
            tvPublisher.setText(book.publisher);
            tvDate.setText(book.publishedDate);
        }

        @Override
        public void onClick(View view) {
            int position  = getAdapterPosition();
            Book selectedPosition = books.get(position);
            Intent intent = new Intent(view.getContext(),BookDetailActivity.class);
            intent.putExtra("Book",selectedPosition);
            view.getContext().startActivity(intent);

        }
    }
}
