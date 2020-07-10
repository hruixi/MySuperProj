package rui.com.crashlog.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import rui.com.crashlog.R;
import rui.com.crashlog.widget.customVIew.BezierCurves_0;

public class ValueAnimFragment extends Fragment {

    private View contentView = null;

    public ValueAnimFragment() {
        // Required empty public constructor
    }

    public static ValueAnimFragment newInstance() {
        ValueAnimFragment fragment = new ValueAnimFragment();
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
        contentView = inflater.inflate(R.layout.fragment_bezier_curves_0, container, false);
        initView();
        return contentView;
    }

    public void initView()
    {}
}
