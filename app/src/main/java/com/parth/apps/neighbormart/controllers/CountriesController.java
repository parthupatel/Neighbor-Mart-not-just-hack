package com.apps.neighbormart.controllers;

import com.apps.neighbormart.classes.CountriesModel;

import io.realm.Realm;

/**
 * Created by Droideve on 7/12/2017.
 */

public class CountriesController {

    public static CountriesModel findByDialCode(String code) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(CountriesModel.class).equalTo("dial_code", code).findFirst();
    }

}
