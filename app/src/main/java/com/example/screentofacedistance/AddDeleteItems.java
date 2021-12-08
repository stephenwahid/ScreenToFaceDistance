package com.example.screentofacedistance;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class AddDeleteItems extends AppCompatActivity {

    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    EditText editText;
    Button button;
    ListView listView;
    Database db = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delete_items);
        getSupportActionBar().setTitle("View Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.itemlist);
        editText = (EditText) findViewById(R.id.addtext);
        button =  (Button) findViewById(R.id.addbutton);
        db = new Database(this, "", null, 1);
        Cursor cr = db.viewComments();
        arrayList = new ArrayList<>();
        while(cr.moveToNext()) {
            arrayList.add(cr.getString(1));
        }
        arrayAdapter = new ArrayAdapter<String>(AddDeleteItems.this, android.R.layout.simple_list_item_multiple_choice, arrayList);
        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                arrayList.add(editText.getText().toString());
                db.insertComment(editText.getText().toString());
                editText.setText("");
                arrayAdapter.notifyDataSetChanged();

            }
        };
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                int count = listView.getCount();
                for (int item = count-1; item >= 0 ; item--){
                    if(sparseBooleanArray.get(item)){
                        Log.i("CHECKED", arrayList.get(item));
                        arrayAdapter.remove(arrayList.get(item));
                        db.updateComment(arrayList);
                    }

                }
                sparseBooleanArray.clear();
                arrayAdapter.notifyDataSetChanged();

                return false;
            }

        });

        button.setOnClickListener(onClickListener);
        listView.setAdapter(arrayAdapter);
    }
}