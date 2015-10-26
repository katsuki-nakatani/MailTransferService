package com.miruker.lib.mailtransferservice.Utils;


import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateUtils {

    public static Calendar toCalendar(String val, String format) throws ParseException {
        if (TextUtils.isEmpty(val))
            return null;
        else {
            SimpleDateFormat form = new SimpleDateFormat(format);
            Calendar cal = Calendar.getInstance();
            cal.setTime(form.parse(val));
            return cal;
        }
    }
}
