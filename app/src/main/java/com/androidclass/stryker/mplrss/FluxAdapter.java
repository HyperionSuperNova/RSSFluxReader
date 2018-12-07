package com.androidclass.stryker.mplrss;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FluxAdapter extends RecyclerView.Adapter<FluxAdapter.FeedModelViewHolder> {

    List<Flux> l;
    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View fluxView;

        public FeedModelViewHolder(View v) {
            super(v);
            fluxView = v;
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
