package com.androidclass.stryker.mplrss;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FluxAdapter extends RecyclerView.Adapter<FluxAdapter.FeedModelViewHolder> {

    public List<Flux> l;



    public static class FeedModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View fluxView;
        private FragmentManager f;
        private String title_flux;
        private int id_flux;
        private DataAccess db;


        public FeedModelViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            fluxView = v;
        }

        @Override
        public void onClick(View v) {
            db = new DataAccess(v.getContext());
            TextView tv = (TextView)v.findViewById(R.id.titleFlux);
            title_flux = tv.getText().toString();
            id_flux = db.getIdFlux(title_flux);
            Intent t = new Intent(v.getContext(), AfficheRSS.class);
            t.putExtra("id_flux", id_flux);
            v.getContext().startActivity(t);
        }
    }

    public FluxAdapter(List<Flux> flux) {
        l = flux;
    }


    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flux, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final Flux rssFeedModel = l.get(position);
        ((TextView) holder.fluxView.findViewById(R.id.titleFlux)).setText(rssFeedModel.title);
        ((TextView) holder.fluxView.findViewById(R.id.linkFlux)).setText(rssFeedModel.link);
        ((TextView) holder.fluxView.findViewById(R.id.descriptionFlux)).setText(rssFeedModel.description);
    }

    @Override
    public int getItemCount() {
        return l.size();
    }
}
