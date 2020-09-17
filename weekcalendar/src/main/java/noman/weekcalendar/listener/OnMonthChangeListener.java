package noman.weekcalendar.listener;

import org.joda.time.DateTime;

/**
 * Created by gokhan on 7/28/16.
 */
public interface OnMonthChangeListener {

    void onMonthChange(DateTime firstDayOfTheWeek, boolean forward);
}
