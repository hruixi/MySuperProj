package rui.com.crashlog.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import rui.com.crashlog.R;
import rui.com.crashlog.activity.MainActivity;

//public class GridAdapter extends BaseAdapter
public class GridAdapter extends ArrayAdapter<Integer>
{
    private List<ImageView> emojis;
    private Context mContext;
    private int count = 0;
    private List<Integer> imageViewIds;

    private final String TAG = getClass().getSimpleName();

//    public GridAdapter(Context context, List<Integer> imageIds) {
//        mContext = context;
//        imageViewIds = imageIds;
//    }
    public GridAdapter(Context context, int resource, List<Integer> imageIds) {
        super(context, resource, imageIds);
        mContext = context;
        imageViewIds = imageIds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.e("herx___"+TAG, "===============================> getView.position= " +position);
//        Log.e("herx___"+TAG, "===============================> getView.convertView= " +convertView);
        ImageView imageView = null;
        int R_id = getItem(position);
//        Log.e("herx___"+TAG, "===============================> getView.R_id= " + R_id);

        if (null == convertView)
        {
            imageView = new ImageView(mContext);
            imageView.setImageResource(R_id);
        }
        else
        {
            imageView = (ImageView) convertView;

        }
        return imageView;
    }

    @Override
    public int getCount() {
        return imageViewIds.size();
    }

//    @Override
//    public Integer getItem(int position) {
//        return imageViewIds.get(position);
//    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


//    public class AdapterHolder
//    {
//        public LinearLayout layout;
//        public ImageView image;
//
//        public AdapterHolder()
//        {
//
//        }
//    }
}
