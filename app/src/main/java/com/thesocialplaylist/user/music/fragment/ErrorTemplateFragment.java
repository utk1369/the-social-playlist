package com.thesocialplaylist.user.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesocialplaylist.user.music.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ErrorTemplateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ErrorTemplateFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ERROR_DISPLAY_ICON = "errorDisplayIcon";

    private String errorMessage;
    private Integer errorDisplayIcon;

    public ErrorTemplateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param errorMessage Parameter 1.
     * @param errorDisplayIcon Parameter 2.
     * @return A new instance of fragment ErrorTemplateFragment.
     */
    public static ErrorTemplateFragment newInstance(String errorMessage, Integer errorDisplayIcon) {
        ErrorTemplateFragment fragment = new ErrorTemplateFragment();
        Bundle args = new Bundle();
        args.putString(ERROR_MESSAGE, errorMessage);
        args.putInt(ERROR_DISPLAY_ICON, errorDisplayIcon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            errorMessage = getArguments().getString(ERROR_MESSAGE);
            errorDisplayIcon = getArguments().getInt(ERROR_DISPLAY_ICON);
        }
        return inflater.inflate(R.layout.fragment_error_template, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
