package com.beastbikes.android.modules.cycling.club.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beastbikes.android.R;

/**
 * Created by caoxiao on 15/11/30.
 */
public class ClubNoNearByFrag extends Fragment {
    private CreateClubInterface createClubInterface;

    public ClubNoNearByFrag() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_nonearby, container, false);
        Button tv = (Button) view.findViewById(R.id.btn_create_club);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClubInterface.createClub();
            }
        });
        return view;
    }

    public void setCreateClubInterface(CreateClubInterface createClubInterface) {
        this.createClubInterface = createClubInterface;
    }

    interface CreateClubInterface {
        void createClub();
    }
}
