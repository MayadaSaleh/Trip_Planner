package mobile.and.web.iti.navigationdrawer.main.fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mobile.and.web.iti.navigationdrawer.R;

/**
 * Created by Alaa on 2/24/2018.
 */

public class ViewHolder_list {
    View myView ;
    ImageView myImg;
    TextView name;
    TextView address;

    public ViewHolder_list(View V ){
        myView = V ;

    }

    public ImageView getMyImg() {
        if (myImg==null)
        {
            myImg= myView.findViewById(R.id.myImg);
        }

        return myImg;
    }

    public TextView getAddress() {
        if (address==null)
        {
            address= myView.findViewById(R.id.Address);
        }
        return address;
    }

    public TextView getName() {
        if (name==null)
        {
            name= myView.findViewById(R.id.Name);
        }
        return name;
    }
}
