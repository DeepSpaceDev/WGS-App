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

import java.io.IOException;
import java.net.URL;

import onl.deepspace.wgs.interfaces.OnTaskCompletedInterface;
import onl.deepspace.wgs.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FoodMenuFragment extends Fragment implements OnTaskCompletedInterface<Bitmap> {

    View view;
    ImageView foodMenuImg;
    PhotoViewAttacher attacher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        foodMenuImg = (ImageView) view.findViewById(R.id.fragment_foodmenu_menuimg);
        attacher = new PhotoViewAttacher(foodMenuImg);
        attacher.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LoadImageFromWebOperations("https://welfen.eltern-portal.org/files/ep_speisenplan_img-0.jpg");

        return view;
    }

    @SuppressWarnings("SameParameterValue")
    private void LoadImageFromWebOperations(String url) {
        new GetMenuImg(this).execute(url);
    }

    @Override
    public void onTaskCompleted(Bitmap response) {
        foodMenuImg.setImageBitmap(response);
        attacher.update();

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
