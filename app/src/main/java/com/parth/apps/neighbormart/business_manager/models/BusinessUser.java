package com.apps.neighbormart.business_manager.models;

import com.apps.neighbormart.AppController;
import com.apps.neighbormart.classes.Module;
import com.apps.neighbormart.classes.ModulePrivilege;
import com.apps.neighbormart.classes.User;
import com.apps.neighbormart.controllers.stores.StoreController;
import com.apps.neighbormart.controllers.users.UserController;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class BusinessUser extends RealmObject {

    @PrimaryKey
    public int id = 1;

    public User user;
    public RealmList<ModuleB> availableModules;

    public static BusinessUser find(){
        Realm realm = Realm.getInstance(AppController.getBusinessRealmConfig());
        return realm.where(BusinessUser.class).equalTo("id", 1).findFirst();
    }

    public  ModuleB findModule(String name) {
        for (int i = 0; i < availableModules.size(); i++) {
            if(availableModules.get(i).getName().equals(name))
                return availableModules.get(i);
        }
        return  null;
    }


    public ModulePrivilegeB findPrivilege(String module_name, String action) {
        ModuleB module = findModule(module_name);

        for (int i = 0; i < module.getPrivileges().size(); i++) {
            if(module.getPrivileges().get(i).action.equals(action))
                return module.getPrivileges().get(i);
        }
        return  null;
    }


}
