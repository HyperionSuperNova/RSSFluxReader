package com.androidclass.stryker.mplrss;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
        private FragmentManager f;
        private ContentResolver contentResolver;
        private String title_flux;
        private int id_flux;
        private DataAccess db;

        public FeedModelViewHolder(View v) {
            super(v);
            fluxView = v;
            contentResolver = fluxView.getContext().getContentResolver();
            db = new DataAccess(fluxView.getContext());
            title_flux = (fluxView.findViewById(R.id.titleFlux)).toString();
            id_flux = db.getIdFlux(title_flux);
            fluxView.setClickable(true);
            fluxView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO : lancer Fragment avec liste de RSS
                    //Intent i = new Intent(fluxView.getContext(), MainActivity.class);
                    //fluxView.getContext().startActivity(i);
                    f = ((FragmentActivity) fluxView.getContext()).getSupportFragmentManager();
                    FragmentTransaction t = f.beginTransaction();
                    ListeRSS p = ListeRSS.newInstance(id_flux);
                    p.setContentResolver(fluxView.getContext());
                    //TODO : continuer pour lancer Fragment
                    t.add(R.id.liste_fragment_frame, p);
                    t.commit();
                    System.out.println("TEST::::::::::::::::::::::::");
                }
            });
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
