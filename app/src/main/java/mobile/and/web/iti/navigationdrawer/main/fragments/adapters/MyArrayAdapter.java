package mobile.and.web.iti.navigationdrawer.main.fragments.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.add.trip.Trip_DTO;
import mobile.and.web.iti.navigationdrawer.update.view.trips.Users;
import mobile.and.web.iti.navigationdrawer.main.fragments.ViewHolder_list;

/**
 * Created by Alaa on 2/24/2018.
 */

public class MyArrayAdapter extends ArrayAdapter {

    Users[] users;
    ArrayList<Trip_DTO> myTrips;

    Context _context;

    public MyArrayAdapter(@NonNull Context context,int Layout, int resource, @NonNull ArrayList<Trip_DTO> myTrips) {
        super(context,Layout, resource, myTrips);
        this.myTrips = myTrips;

        _context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      View rowView = convertView;
        ViewHolder_list viewHolder ;

        if(rowView==null){
          LayoutInflater  inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          rowView = inflater.inflate(R.layout.rowcustom,parent,false);
          viewHolder =new ViewHolder_list(rowView);
          rowView.setTag(viewHolder);

      }
      else {
            viewHolder = (ViewHolder_list) rowView.getTag();
        }
        if(myTrips.get(position).getPlaceImage() != null)
            viewHolder.getMyImg().setImageBitmap(myTrips.get(position).getPlaceImage());
        else
            viewHolder.getMyImg().setImageResource(R.mipmap.image);
        viewHolder.getName().setText(myTrips.get(position).getName());
        viewHolder.getAddress().setText(myTrips.get(position).getDateStr());


    return  rowView;
    }
}
