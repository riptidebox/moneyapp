package com.example.wheresmymoney.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.singleton.Global;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Global.getInstance().getRecentTheme());
        setContentView(R.layout.activity_settings);
    }

    protected void onResume()
    {
        super.onResume();
        ArrayList<String> themesList = new ArrayList<>();
        themesList.add("светлая тема");
        themesList.add("темная тема");

        final ListView list = findViewById(R.id.themesListView);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, themesList);
        list.setAdapter(adapter2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch(position)
                {
                    case 0:
                        Global.getInstance().displayMessage("светлая тема");
                        break;
                    case 1:
                        Global.getInstance().displayMessage("темная тема");
                        break;
                }
                Global.getInstance().setTheme(position);
                Global.getInstance().savePreferences();
                recreate();
            }
        });
    }
}
