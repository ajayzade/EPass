package com.rawtalent.epass_user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;


public class ChangeNavigationActivities {

    public void startHomeActivity(@NonNull Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public void startUserPassesActivity(@NonNull Context context) {
        Intent intent = new Intent(context, UserPasses.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public void startProfileActivity(@NonNull Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
