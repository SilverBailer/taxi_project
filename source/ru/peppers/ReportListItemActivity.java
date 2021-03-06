package ru.peppers;

import model.Order;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReportListItemActivity extends BalanceActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        Bundle bundle = getIntent().getExtras();
        int index = bundle.getInt("index");
        Order report = TaxiApplication.getDriver(this).getReport(index);

        TextView tv = (TextView) findViewById(R.id.textView1);

        int arraySize = report.toArrayList().size();
        for (int i = 0; i < arraySize; i++) {
            tv.append(report.toArrayList().get(i));
            tv.append("\n");
        }
    }
}
