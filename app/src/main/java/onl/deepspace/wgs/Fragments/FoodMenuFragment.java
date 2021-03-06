package onl.deepspace.wgs.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.net.URL;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;
import onl.deepspace.wgs.interfaces.OnTaskCompletedInterface;

public class FoodMenuFragment extends Fragment implements OnTaskCompletedInterface<Bitmap> {

    View view;
    ImageView foodMenuImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        foodMenuImg = (ImageView) view.findViewById(R.id.fragment_foodmenu_menuimg);

        LoadImageFromWebOperations("https://welfen.eltern-portal.org/files/ep_speisenplan_img-0.jpg");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        firebaseAnalytics.logEvent(Helper.EVENT_SHOW_FOOD_MENU, new Bundle());
    }

    @SuppressWarnings("SameParameterValue")
    private void LoadImageFromWebOperations(String url) {
        new GetMenuImg(this).execute(url);
    }

    @Override
    public void onTaskCompleted(Bitmap response) {
        foodMenuImg.setImageBitmap(response);
    }

    private class GetMenuImg extends AsyncTask<String, Void, Bitmap> {

        OnTaskCompletedInterface<Bitmap> taskCompletedInterface;

        GetMenuImg(OnTaskCompletedInterface<Bitmap> taskCompletedInterface){
            this.taskCompletedInterface = taskCompletedInterface;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            taskCompletedInterface.onTaskCompleted(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(new URL(strings[0]).openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;

        }

    }
}
