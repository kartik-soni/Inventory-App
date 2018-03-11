package com.example.kartik.inventoryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by KARTIK on 3/11/2018.
 */

public class EditorsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
            MenuItem deleteOneItemMenuItem = menu.findItem(R.id.delete_item);
            MenuItem deleteAllMenuItem = menu.findItem(R.id.delete_all_data);
            MenuItem orderMenuItem = menu.findItem(R.id.order);
            deleteOneItemMenuItem.setVisible(false);
            deleteAllMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.add_dummy_data:
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.delete_all_data:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}