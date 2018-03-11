package com.example.kartik.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.kartik.inventoryapp.data.StockContract;
import com.example.kartik.inventoryapp.data.StockProvider;
import com.example.kartik.inventoryapp.data.StocksDbHelper;

/**
 * Created by KARTIK on 3/11/2018.
 */

public class EditorsActivity extends AppCompatActivity {

    EditText productName;
    EditText productPrice;
    EditText productQuantity;
    EditText supplierName;
    EditText supplierPhone;
    long currentItemId;
    ImageButton decreaseProductQuantity;
    ImageButton increaseProductQuantity;
    Button imageBrowse;
    ImageView imageView;
    Uri actualUri;
    private static final String LOG_TAG = EditorsActivity.class.getCanonicalName();
    private static final int PERMISSIONS_EXTERNAL_STORAGE = 1;
    private StocksDbHelper dbHelper;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean infoItemHasChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        productName = findViewById(R.id.product_name_edit);

        productPrice = findViewById(R.id.price_edit);
        productQuantity = findViewById(R.id.quantity_edit);
        supplierName = findViewById(R.id.supplier_name_edit);
        supplierPhone =  findViewById(R.id.supplier_phone_edit);
        decreaseProductQuantity = findViewById(R.id.decrease_quantity);
        increaseProductQuantity = findViewById(R.id.increase_quantity);
        imageBrowse = findViewById(R.id.select_image);
        imageView = findViewById(R.id.image_view);

        dbHelper = new StocksDbHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        if (currentItemId == 0) {
            setTitle(getString(R.string.add_new_item));
        } else {
            setTitle(getString(R.string.edit_item));
            EditItem(currentItemId);
        }

        decreaseProductQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrementInQuantity();
                infoItemHasChanged = true;
            }
        });

        increaseProductQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementInQuantity();
                infoItemHasChanged = true;
            }
        });

        imageBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseImage();
                infoItemHasChanged = true;
            }
        });
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
        switch (item.getItemId()) {
            case R.id.save:
                if (!saveData()) {
                    return true;
                }
                finish();
                return true;
            case android.R.id.home:
                if (!infoItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorsActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                unsavedDialoguePrompt(discardButtonClickListener);
                return true;
            case R.id.order:
                // dialog with phone and email
                OrderConfirmationDialog();
                return true;
            case R.id.delete_item:
                // delete one item
                deleteDialoguePrompt(currentItemId);
                return true;
            case R.id.delete_all_data:
                //delete all data
                deleteDialoguePrompt(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void EditItem(long itemId) {
        Cursor cursor = dbHelper.readItem(itemId);
        cursor.moveToFirst();
        productName.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_NAME)));
        productPrice.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_PRICE)));
        productQuantity.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY)));
        supplierName.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_NAME)));
        supplierPhone.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_PHONE)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_IMAGE))));
        productName.setEnabled(false);
        productPrice.setEnabled(false);
        supplierName.setEnabled(false);
        supplierPhone.setEnabled(false);
        imageBrowse.setEnabled(false);
    }

    private boolean saveData() {
        boolean check = true;
        if (!setValue(productName, "name")) {
            check = false;
        }
        if (!setValue(productPrice, "price")) {
            check = false;
        }
        if (!setValue(productQuantity, "quantity")) {
            check = false;
        }
        if (!setValue(supplierName, "supplier name")) {
            check = false;
        }
        if (!setValue(supplierPhone, "supplier phone")) {
            check = false;
        }
        if (actualUri == null && currentItemId == 0) {
            check = false;
            imageBrowse.setError("Missing image");
        }
        if (!check) {
            return false;
        }

        if (currentItemId == 0) {
            StockProvider item = new StockProvider(
                    productName.getText().toString().trim(),
                    productPrice.getText().toString().trim(),
                    Integer.parseInt(productQuantity.getText().toString().trim()),
                    supplierName.getText().toString().trim(),
                    supplierPhone.getText().toString().trim(),
                    actualUri.toString());
            dbHelper.insertItem(item);
        } else {
            int quantity = Integer.parseInt(productQuantity.getText().toString().trim());
            dbHelper.updateItem(currentItemId, quantity);
        }
        return true;
    }

    private void decrementInQuantity() {
        String quantity = productQuantity.getText().toString();
        int previousQuantity;
        if (quantity.isEmpty()) {
            return;
        } else if (quantity.equals("0")) {
            return;
        } else {
            previousQuantity = Integer.parseInt(quantity);
            productQuantity.setText(String.valueOf(previousQuantity - 1));
        }
    }

    private void incrementInQuantity() {
        String quantity = productQuantity.getText().toString();
        int previousQuantity;
        if (quantity.isEmpty()) {
            return;
        } else if (quantity.equals("0")) {
            return;
        } else {
            previousQuantity = Integer.parseInt(quantity);
            productQuantity.setText(String.valueOf(previousQuantity + 1));
        }
    }


    public void BrowseImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void OrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.place_order);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhone.getText().toString().trim()));
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void unsavedDialoguePrompt(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_prompt);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteDialoguePrompt(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.askToDelete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (itemId == 0) {
                    deleteAllData();
                } else {
                    deleteOneItem(itemId);
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int deleteAllData() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(StockContract.StockEntry.TABLE_NAME, null, null);
    }

    private int deleteOneItem(long itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = StockContract.StockEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(itemId) };
        int rowsDeleted = database.delete(
                StockContract.StockEntry.TABLE_NAME, selection, selectionArgs);
        return rowsDeleted;
    }

    private boolean setValue(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError(getString(R.string.product_missing) + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (!infoItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        unsavedDialoguePrompt(discardButtonClickListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_EXTERNAL_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}