package com.example.voicerec;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHistory extends Fragment {

    ListView OperationsLstV;
    List<Operation> operations;
    DbHelper dbhelper;
    ArrayAdapter<String> arrayAdapter;
    Button BtnClear;
    Button BtnBack;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHistory newInstance(String param1, String param2) {
        FragmentHistory fragment = new FragmentHistory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        BtnClear = getActivity().findViewById(R.id.clearBtn);
        BtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int l = 0;
                l = dbhelper.clearOperations();
                if (l > 0) {
                    operations.clear();
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Cleared", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "History is empty ", Toast.LENGTH_SHORT).show();
            }
        });

        OperationsLstV = getActivity().findViewById(R.id.OpLst);
        dbhelper = new DbHelper(getContext());
        operations = dbhelper.getAllOperations();
        arrayAdapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, operations);
        OperationsLstV.setAdapter(arrayAdapter);

        OperationsLstV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Operation op = operations.get(position);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.workings = op.Formula;
                mainActivity.setNewWorkings(mainActivity.workings);
            }
        });

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setBtn2Icon();

    }
}