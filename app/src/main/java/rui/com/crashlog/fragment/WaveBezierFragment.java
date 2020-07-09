package rui.com.crashlog.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rui.com.crashlog.R;

public class WaveBezierFragment extends Fragment
{
    private View contentView = null;

    public WaveBezierFragment() {
        // Required empty public constructor
    }

    public static WaveBezierFragment newInstance() {
        WaveBezierFragment fragment = new WaveBezierFragment();
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
        contentView = inflater.inflate(R.layout.fragment_bezier_curves_4, container, false);
        initView();
        return contentView;
    }

    public void initView()
    {}
}
