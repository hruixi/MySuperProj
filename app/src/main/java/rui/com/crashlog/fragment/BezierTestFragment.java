package rui.com.crashlog.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import rui.com.crashlog.R;

public class BezierTestFragment extends Fragment
{
    private View contentView = null;

    public BezierTestFragment() {
        // Required empty public constructor
    }

    public static BezierTestFragment newInstance() {
        BezierTestFragment fragment = new BezierTestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_bezier_curves_2, container, false);
        initView();
        return contentView;
    }

    public void initView()
    {}
}
