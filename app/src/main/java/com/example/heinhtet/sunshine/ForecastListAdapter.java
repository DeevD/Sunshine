package com.example.heinhtet.sunshine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by heinhtet on 4/10/17.
 */

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ForecastViewHolder> {

    String[]mWeatherData = null;
    Context mContext;

  public ForecastAdapterOnClickHandler mclickHandler;



    public ForecastListAdapter(Context context,ForecastAdapterOnClickHandler clickHandler) {
        mclickHandler = clickHandler;
    }

    public interface ForecastAdapterOnClickHandler
    {
        void onClick(String weatherForDay);
    }



    @Override
    public ForecastListAdapter.ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.forcast_list_items,parent,false);
        ForecastViewHolder viewHolder = new ForecastViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ForecastListAdapter.ForecastViewHolder holder, int position) {
        String weatherForDay = mWeatherData[position];
        holder.mWeatherTextView.setText(weatherForDay);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData == null)
        {
            return 0;
        }
        else
        {
            return mWeatherData.length;
        }

    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mWeatherTextView ;
        ImageView weather_data_image;
        public ForecastViewHolder(View itemView) {
            super(itemView);
//            mWeatherTextView = (TextView) itemView.findViewById(R.id.weather_data_tv);

//            weather_data_image = (ImageView)itemView.findViewById()
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String weatherForDay = mWeatherData[position];

            mclickHandler.onClick(weatherForDay);
        }
    }
    public void setWeatherData(String[] data)
    {
        mWeatherData = data;
        notifyDataSetChanged();
    }


}
