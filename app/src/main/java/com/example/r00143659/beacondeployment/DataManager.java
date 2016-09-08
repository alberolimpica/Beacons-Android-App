package com.example.r00143659.beacondeployment;

import io.realm.Realm;
import io.realm.RealmResults;

public abstract class DataManager {

    private static Realm realm = Realm.getDefaultInstance();

    public static RealmResults<THProximity> findAll(){
        return realm.where(THProximity.class).findAll();
    }

    public static THProximity findOne(String id){
        return realm.where(THProximity.class).equalTo("id", id).findFirst();
    }

    public static void save(final THProximity thProximity){
        realm.beginTransaction();

        THProximity proximity = findOne(thProximity.getId());

        if(proximity != null)
            proximity.setStatus(thProximity.getStatus());
        else
            realm.copyToRealm(thProximity);

        realm.commitTransaction();
    }

    public static void updateStatus(final THProximity thProximity, final int status){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                thProximity.setStatus(status);
                save(thProximity);
            }
        });
    }
}
