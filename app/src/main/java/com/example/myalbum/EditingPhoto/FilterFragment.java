package com.example.myalbum.EditingPhoto;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.myalbum.R;

import ja.burhanrashid52.photoeditor.PhotoFilter;

class Filter {
    Integer avatar;
    String description;
    PhotoFilter filterCode;
}

public class FilterFragment extends Fragment implements FragmentCallbacks {
    Context context = null;
    PhotoEditorHandler main;

    static Integer[] avatar =
            {
                    R.drawable.edit_filter_original,
                    R.drawable.edit_filter_auto_fix,
                    R.drawable.edit_filter_b_n_w,
                    R.drawable.edit_filter_contrast
            };
    static String[] description =
            {
                    "Original",
                    "Auto fix",
                    "Black & White",
                    "Contrast"
            };
    static PhotoFilter[] filterOptions =
            {
                    PhotoFilter.NONE,
                    PhotoFilter.AUTO_FIX,
                    PhotoFilter.BLACK_WHITE,
                    PhotoFilter.CONTRAST
            };

    public static FilterFragment newInstance()
    {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            context = getActivity();
            main = (PhotoEditorHandler) getActivity();
        }
        catch (IllegalStateException e)
        {
            throw new IllegalStateException( "MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.edit_filter_fragment_layout, null);

        HorizontalScrollView scrollView = layout.findViewById(R.id.scrollView);

        FilterAdapter adapter = new FilterAdapter(context,R.layout.edit_filter_adapter_layout,avatar,description,filterOptions);

        return layout;
    }

    @Override
    public void onMsgFromMainToFragment(Bundle bundle) {

    }
}
