package com.beastbikes.android.modules.cycling.sections.ui;

import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.sections.dto.SectionListDTO;

import java.util.List;

/**
 * Created by caoxiao on 16/4/5.
 */
public abstract class SectionBaseFragment extends SessionFragment {

    public abstract void notifyDataSetChanged(List<SectionListDTO> sectionList);

    public abstract void filterFailed();

    public abstract void getLocationFail(String errorMsg);

    public abstract void noData(String errorMsg);
}
