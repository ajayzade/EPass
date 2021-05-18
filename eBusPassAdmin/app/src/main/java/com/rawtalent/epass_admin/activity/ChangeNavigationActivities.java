package com.rawtalent.epass_admin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class ChangeNavigationActivities {

    public void startHomeActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public void startAccountVerificationActivity(Context context) {
        Intent intent = new Intent(context, AccountVerification.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
    public void startEpassApplicationActivity(Context context) {
        Intent intent = new Intent(context, EpassApplications.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }




}
