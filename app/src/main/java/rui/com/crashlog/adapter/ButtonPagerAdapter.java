package rui.com.crashlog.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class ButtonPagerAdapter extends PagerAdapter
        implements View.OnClickListener
{
    private List<Button> buttons;
    private Context mContext;
    private final String TAG = getClass().getSimpleName();

    public ButtonPagerAdapter(Context context, List<Button> buttons)
    {
        mContext = context;
        this.buttons = buttons;
        updateButtons();
    }

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
//        super.instantiateItem(container, position);
//        Log.e("herx___" + TAG, "======================================> instantiateItem().position= " + position);
        container.addView(buttons.get(position));
        return buttons.get(position);
    }

    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        Button b = (Button) object;
        container.removeView(b);
    }

    @Override
    public boolean isViewFromObject(View v, Object o)
    {
        return v == o;
    }

    @Override
    public int getCount()
    {
        return buttons.size();
    }

    public void updateButtons()
    {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

        }
    }
}
