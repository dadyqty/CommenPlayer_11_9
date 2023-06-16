package com.d.commenplayer;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class Activity_contorller {
    private static List<Activity> activities = new ArrayList<>();

    public static void add_activities (Activity activity)
    {
        activities.add(activity);
    }

    public static void remove_activities(Activity activity)
    {
        activities.remove(activity);
    }
    public static void end_the_application()
    {
        for (Activity activity:activities
        ) {
            if(!activity.isFinishing())
            {
                activity.finish();
            }
        }
    }
}
