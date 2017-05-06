package com.example.heinhtet.sunshine.sync;

import android.content.Context;
import android.content.Intent;

/**
 * Created by heinhtet on 5/2/17.
 */

public class SunshineSyncUtils {
    public SunshineSyncUtils(){

    }
    public static void startImmediateSync(Context context)
    {
        Intent startService = new Intent(context,SunshineSyncIntentService.class);
        context.startService(startService);
    }
}
