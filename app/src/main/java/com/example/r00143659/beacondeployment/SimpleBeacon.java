package com.example.r00143659.beacondeployment;

/**
 * This class createss a BeaconObject object, for the BeaconActivity to interact with
 */


public class SimpleBeacon {

    private String namespace;
    private String id;
    private Double distance;

    public SimpleBeacon(){
    }
    public SimpleBeacon(String id, String namespace, Double distance) {
        this.namespace = namespace;
        this.id = id;
        this.distance = distance;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getNamespace(){
        return namespace;
    }
    public void setNamespace(String namespace){
        this.namespace = namespace;
    }
    public Double getDistance(){
        return distance;
    }
    public void setDistance(Double distance){
        this.distance = distance;
    }
}
