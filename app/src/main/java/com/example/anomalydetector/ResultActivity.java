package com.example.anomalydetector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity  extends AppCompatActivity {

    LineChart mChart;
    TextView txt;
    float[] list;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mChart=findViewById(R.id.line_chart);
        txt=findViewById(R.id.txt);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        list=intent.getFloatArrayExtra("data");


        if(intent.getStringExtra("value").equals("Normal")) {
            txt.setTextColor(Color.GREEN);
            txt.setText("Your ECG report is "+intent.getStringExtra("value"));
        }
        else {
            txt.setTextColor(Color.RED);
            txt.setText("You have " + intent.getStringExtra("value"));
        }

        mChart.setNoDataText("Loading...");
        mChart.setTouchEnabled(false);
        mChart.getDescription().setEnabled(false);

        ArrayList<Entry> y=new ArrayList<>();
        ArrayList<Entry> x=new ArrayList<>();

        for(int i=1;i<=140;i++)
            x.add(new Entry(i-1,i));

        for(int i=0;i<list.length;i++)
            y.add(new Entry(i,list[i]));

        LineDataSet set = new LineDataSet(y,"ECG");
        //set.setColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setDrawCircles(false);
        set.setValueTextSize(9f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set);

        LineData lineData = new LineData(dataSets);

        mChart.setData(lineData);

        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisRight().setEnabled(false);

//        YAxis yAxis1 =mChart.getAxisRight();
//        yAxis1.setAxisMinimum(-6f);
        YAxis yAxis2 =mChart.getAxisLeft();
        yAxis2.setAxisMinimum(-8f);
        yAxis2.setAxisMaximum(8f);

        mChart.setNoDataTextColor(Color.BLACK);
        mChart.getAxisLeft().setTextColor(Color.BLACK);
        mChart.getXAxis().setTextColor(Color.BLACK);
        l.setTextColor(Color.BLACK);



    }
}
