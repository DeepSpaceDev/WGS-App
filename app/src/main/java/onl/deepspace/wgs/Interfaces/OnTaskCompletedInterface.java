package onl.deepspace.wgs.Interfaces;

/**
 * Created by Sese on 06.02.2016.
 * Interface for calling method when AsyncTask is completed
 */
public interface OnTaskCompletedInterface<T> {

    void onTaskCompleted(T response);

}
