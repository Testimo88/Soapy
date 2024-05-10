package com.android.soapy.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class FragmentUtils {
    public static void replaceFragment(FragmentActivity activity, int viewID, Fragment fragment){
        activity.getSupportFragmentManager().beginTransaction().replace(viewID, fragment).commit();
    }
    public static void show(FragmentActivity activity, int viewID,Fragment fragment){
        activity.getSupportFragmentManager().beginTransaction().add(viewID, fragment).show(fragment).commit();
    }
    public static void hide(FragmentActivity activity,Fragment fragment){
        activity.getSupportFragmentManager().beginTransaction().hide(fragment).commit();
    }

}
