package mobile.and.web.iti.navigationdrawer.add.trip.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import mobile.and.web.iti.navigationdrawer.R;

/**
 * Created by Hesham on 3/6/2018.
 */

public class AutoAdapter extends RecyclerView.Adapter<AutoAdapter.PredictionHolder>  implements Filterable {

    private ArrayList<AT_Place> myResultList;
    private GoogleApiClient myApiClient;
    private LatLngBounds myBounds;
    private AutocompleteFilter myACFilter;

    private Context myContext;
    private int layout;


    public AutoAdapter(Context context, int resourse, GoogleApiClient googleApiClient
            , LatLngBounds bounds, AutocompleteFilter filter) {
        myContext = context;
        layout = resourse;
        myApiClient = googleApiClient;
        myBounds = bounds;
        myACFilter = filter;
    }


    private void setMyBounds(LatLngBounds bounds) {
        myBounds = bounds;
    }

    @Override
    public Filter getFilter() {
    Filter filter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if(charSequence != null){
                myResultList = getAutoComplete(charSequence);
                if(myResultList != null){
                    results.values = myResultList;
                    results.count = myResultList.size();
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if(filterResults != null && filterResults.count > 0){
                notifyDataSetChanged();
            }else{

            }
        }
    };

    return filter;
    }

    @Override
    public PredictionHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(layout , parent , false);
        PredictionHolder mPredictionHolder = new PredictionHolder(convertView);
        return mPredictionHolder;
    }

    @Override
    public void onBindViewHolder( PredictionHolder holder, int position) {
        holder.myPrediction.setText(myResultList.get(position).description);
    }

    @Override
    public int getItemCount() {
        if(myResultList != null)
            return myResultList.size();
        else
        return 0;
    }

    public AT_Place getItem(int position) {return myResultList.get(position); }

    private ArrayList<AT_Place> getAutoComplete(CharSequence charSequence){
        if(myApiClient.isConnected()){
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                    .getAutocompletePredictions(myApiClient , charSequence.toString() , myBounds , myACFilter);

            AutocompletePredictionBuffer autocompletePredictions = results.await(60 , TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();
            if(!status.isSuccess()){
//                Toast.makeText(myContext, "Error Contacting API" + status.toString(), Toast.LENGTH_SHORT).show();
                autocompletePredictions.release();
                return null;
            }

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            Log.i("MyAdapter count" , resultList.size()+"");
            while (iterator.hasNext()){
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new AT_Place(prediction.getPlaceId() , prediction.getFullText(null)));
            }
            autocompletePredictions.release();
            return resultList;
        }
        Log.i("MyAdapter Error" , "Get Autocomplete returns null !!");
        return null;
    }

    public class AT_Place{
        public CharSequence placeId;
        public CharSequence description;

        AT_Place(CharSequence placeId , CharSequence description){
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }

    public class PredictionHolder extends RecyclerView.ViewHolder{
        private TextView myPrediction;
        private RelativeLayout myRow;


        public PredictionHolder(View itemView) {
            super(itemView);
            myPrediction = itemView.findViewById(R.id.address);
            myRow = itemView.findViewById(R.id.autocompleteRow);
        }

    }


}
