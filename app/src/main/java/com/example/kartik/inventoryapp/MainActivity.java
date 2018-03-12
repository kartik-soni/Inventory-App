package com.example.kartik.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.kartik.inventoryapp.data.StockProvider;
import com.example.kartik.inventoryapp.data.StocksDbHelper;

public class MainActivity extends AppCompatActivity {
    StocksDbHelper dbHelper;
    StockAdapter stockAdapter;
    int scrollFlag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new StocksDbHelper(this);

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
        Cursor cursor = dbHelper.readStock();

        stockAdapter = new StockAdapter(this, cursor);
        listView.setAdapter(stockAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) return;
                final int currentVisibleItem = view.getFirstVisiblePosition();
                if (currentVisibleItem > scrollFlag) {
                    fab.show();
                } else if (currentVisibleItem < scrollFlag) {
                    fab.hide();
                }
                scrollFlag = currentVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_dummy_data:
                addDummyData();
                stockAdapter.swapCursor(dbHelper.readStock());
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
        stockAdapter.swapCursor(dbHelper.readStock());
    }

    @Override
    protected void onResume() {
        super.onResume();
        stockAdapter.swapCursor(dbHelper.readStock());
    }

    private void addDummyData() {

        StockProvider peanutButter = new StockProvider(
                "Peanut Butter",
                "Rs.140",
                111,
                "Pintola Foods Co.",
                "+91 5123456789",
                "android.resource://com.example.kartik.inventoryapp/drawable/peanut_butter");
        dbHelper.insertItem(peanutButter);

        StockProvider egg = new StockProvider(
                "Eggs",
                "Rs.5",
                100,
                "Republic Of Chicken",
                "+91 5987643215",
                "android.resource://com.example.kartik.inventoryapp/drawable/eggs");
        dbHelper.insertItem(egg);

        StockProvider cheese = new StockProvider(
                "Cheese",
                "Rs.60",
                51,
                "Verka Diary",
                "+91 9876542584",
                "android.resource://com.example.kartik.inventoryapp/drawable/cheese");
        dbHelper.insertItem(cheese);

        StockProvider milk = new StockProvider(
                "Milk",
                "Rs.45",
                51,
                "Verka Diary",
                "+91 5435843215",
                "android.resource://com.example.kartik.inventoryapp/drawable/milk");
        dbHelper.insertItem(milk);

        StockProvider cola = new StockProvider(
                "Coca Cola",
                "Rs.40",
                50,
                "American Beverage",
                "+91 9876543215",
                "android.resource://com.example.kartik.inventoryapp/drawable/cola");
        dbHelper.insertItem(cola);

    }
}