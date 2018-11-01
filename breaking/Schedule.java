package scripts.api.breaking;

import scripts.api.webwalker.shared.Pair;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Spencer on 10/26/2017.
 */
public class Schedule {

    public Calendar date;
    public long playTime;
    public ArrayList<Pair<Long, Long>> playTimes = new ArrayList<>();

    public Schedule(Calendar date, long playTime, ArrayList<Pair<Long, Long>> playTimes) {
        this.date = date;
        this.playTime = playTime;
        this.playTimes = playTimes;
    }

    public Schedule(Calendar date, long playTime) {
        this.date = date;
        this.playTime = playTime;
        this.playTimes = new ArrayList<>();
    }

}
