package mobile.and.web.iti.navigationdrawer.main.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mobile.and.web.iti.navigationdrawer.R;

/**
 * Created by Alaa on 2/24/2018.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    View myView ;
    TextView desc ;
    ImageView myImg ;
    ImageView overFlow;

    public ViewHolder(View V )
    {      super(V);
        myView=V;
    }


    public ImageView getOverFlow() {
        if(overFlow==null){
            overFlow=myView.findViewById(R.id.overFlow);
        }
        return overFlow;
    }

    public TextView getDesc() {
        if(desc==null){
            desc = myView.findViewById(R.id.Name);
        }
        return desc;
    }

    public ImageView getMyImg() {
        if(myImg==null){
            myImg=myView.findViewById(R.id.myImg);
        }

        return myImg;
    }
}
