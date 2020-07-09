package rui.com.crashlog.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rui.com.crashlog.R;

public class PathAnimFragment extends android.support.v4.app.Fragment
{
    private View contentView = null;

    public PathAnimFragment() {
        // Required empty public constructor
    }

    public static PathAnimFragment newInstance() {
        PathAnimFragment fragment = new PathAnimFragment();
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
        contentView = inflater.inflate(R.layout.fragment_bezier_curves_5, container, false);
        initView();
        return contentView;
    }

    public void initView()
    {}
}
