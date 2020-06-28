package com.example.myworld.adapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {

    private LatLng position;
    private String name, imageUri;

    public MyItem(LatLng position) {
        this.position = position;
    }

    public MyItem(LatLng position, String name, String imageUri) {
        this.position = position;
        this.name = name;
        this.imageUri = imageUri;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }


    public String getImageUri() {
        return imageUri;
    }


    public String getName() {
        return name;
    }
}
