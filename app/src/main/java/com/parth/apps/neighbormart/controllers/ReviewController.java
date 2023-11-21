package com.apps.neighbormart.controllers;

import com.apps.neighbormart.classes.Guest;
import com.apps.neighbormart.classes.Review;
import com.apps.neighbormart.controllers.sessions.GuestController;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Droideve on 12/25/2017.
 */

public class ReviewController {


    public static boolean isRated(int store_id) {

        Guest guest = GuestController.getGuest();
        Realm mRealm = Realm.getDefaultInstance();

        try {

            RealmResults<Review> result = mRealm.where(Review.class)
                    .equalTo("store_id", store_id)
                    .equalTo("guest_id", guest.getId()).findAll();


            if (result.isLoaded() && result.isValid() && result.size() > 0)
                return true;

        } catch (Exception e) {

        }

        return false;
    }

}
