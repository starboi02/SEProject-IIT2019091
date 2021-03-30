package com.example.anomalydetector;

import java.util.List;

public class ECGData {

    String name,value,time;
    List<Float> data;

    ECGData(){

    }

    ECGData(String name,List<Float> data,String value,String time){
        this.name=name;
        this.data=data;
        this.value=value;
        this.time=time;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public List<Float> getData() {
        return data;
    }

    public String getValue() {
        return value;
    }


}
