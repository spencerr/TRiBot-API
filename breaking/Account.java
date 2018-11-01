package scripts.api.breaking;

import scripts.api.data.Bag;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Spencer on 10/26/2017.
 */
public class Account {

    public String username;
    public String password;
    public boolean active = false;
    public HashMap<Calendar, Schedule> schedule = new HashMap<>();
    public Bag bag = new Bag();

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
