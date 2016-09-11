package onl.deepspace.wgs.fragments.representation;

/**
 * Created by Sebastian on 11.09.2016.
 */

public class RepresentationItem {

    public String subject, action, room;
    int time;

    public RepresentationItem(String subject, String action, int time, String room){
        this.subject = subject;
        this.action = action;
        this.time = time;
        this.room = room;
    }

}
