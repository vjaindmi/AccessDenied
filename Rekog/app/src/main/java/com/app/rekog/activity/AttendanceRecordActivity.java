package com.app.rekog.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rekog.R;
import com.app.rekog.adapter.AttendanceRecordAdapter;
import com.app.rekog.beans.users.TimeStamp;
import com.app.rekog.beans.users.User;
import com.app.rekog.database.RealmDatabaseController;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by bkhera on 2/23/2018.
 */

public class AttendanceRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailEd;
    private RecyclerView mRecyclerView;
    private TextView mNameTv, mUserEmailTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_record);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.attendance_record);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onBackPressed();
            }
        });
        mEmailEd = findViewById(R.id.email_et);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNameTv = findViewById(R.id.user_name_tv);
        mUserEmailTv = findViewById(R.id.user_email_tv);
        findViewById(R.id.submit_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                showRecords();
                break;
        }
    }

    private void showRecords() {
        User temp = RealmDatabaseController.getInstance().getUser(mEmailEd.getText().toString());
        if (temp != null) {
            RealmList<TimeStamp> realmList = temp.getTime();

            ArrayList<TimeStamp> arrayList = new ArrayList<>();
            arrayList.addAll(realmList);
            mRecyclerView.setAdapter(new AttendanceRecordAdapter(arrayList));
            mNameTv.setText(temp.getName());
            mUserEmailTv.setText(temp.getEmail());
        } else {
            Toast.makeText(this, "No Record Found", Toast.LENGTH_SHORT).show();
        }

    }
}
