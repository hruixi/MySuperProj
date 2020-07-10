package rui.com.crashlog.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import rui.com.crashlog.R;
import rui.com.crashlog.widget.customVIew.BezierCurves_0;
import rui.com.crashlog.widget.customVIew.BezierCurves_1;


public class WritingFragment extends Fragment
{
    private View contentView = null;
    private Button clear = null;
    private BezierCurves_1 bezierCurves1 = null;

    public WritingFragment() {
        // Required empty public constructor
    }

    public static WritingFragment newInstance() {
        WritingFragment fragment = new WritingFragment();
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
        contentView = inflater.inflate(R.layout.fragment_bezier_curves_1, container, false);
        clear = contentView.findViewById(R.id.clearBtn);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        return contentView;
    }

    public void clear()
    {
        bezierCurves1 = contentView.findViewById(R.id.bezierCurves1);
        bezierCurves1.clear();
    }
}
