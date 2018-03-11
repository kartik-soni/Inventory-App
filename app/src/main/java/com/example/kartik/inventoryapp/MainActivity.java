package com.example.kartik.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.kartik.inventoryapp.data.StocksDbHelper;

public class MainActivity extends AppCompatActivity {
    StocksDbHelper dbHelper;
    StockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorsActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_dummy_data:
                /*addDummyData();
                adapter.swapCursor(dbHelper.readStock());*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickListItem(long id) {
        Intent intent = new Intent(this, EditorsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickCart(long id, int quantity) {
        dbHelper.sellOneItem(id, quantity);
        adapter.swapCursor(dbHelper.readStock());
    }
}
