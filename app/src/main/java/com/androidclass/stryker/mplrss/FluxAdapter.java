package com.androidclass.stryker.mplrss;

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
    public static class FeedModelViewHolder extends RecyclerView.ViewHolder implements ListeRSS.OnFragmentInteractionListener, ListeRSSFav.OnFragmentInteractionListener, ListeRSSSearch.OnFragmentInteractionListener {
        private View fluxView;
        private FragmentManager f;

        public FeedModelViewHolder(View v) {
            super(v);
            fluxView = v;
            fluxView.setClickable(true);
            fluxView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO : lancer Fragment avec liste de RSS
                    Intent i = new Intent(fluxView.getContext(), MainActivity.class);
                    fluxView.getContext().startActivity(i);
                    f = ((FragmentActivity) fluxView.getContext()).getSupportFragmentManager();
                    FragmentTransaction t = f.beginTransaction();
                    ListeRSS p = ListeRSS.newInstance();
                    p.setContentResolver(fluxView.getContext());
                    //TODO : continuer pour lancer Fragment
                    t.add(R.id.liste_fragment_frame, p);
                    t.commit();
                    System.out.println("TEST::::::::::::::::::::::::");
                }
            });
        }

        @Override
        public void onFragmentInteraction(Uri uri) {

        }

        @Override
        public void onRSSSelection(String title) {
            UnRSS u = UnRSS.newInstance(title, false);
            u.setContentResolver(fluxView.getContext());
            FragmentTransaction t = f.beginTransaction();
            //t.replace(R.id.liste_fragment_frame, ListPaysFragment.newInstance());
            t.replace(R.id.liste_fragment_frame, u);
            t.addToBackStack(null);
            //t.addToBackStack(null);FragmentTransaction t;
            t.commit();
        }

        @Override
        public void onRSSSelectionFav(String title) {
            UnRSS u = UnRSS.newInstance(title, true);
            u.setContentResolver(fluxView.getContext());
            FragmentTransaction t = f.beginTransaction();
            //t.replace(R.id.liste_fragment_frame, ListPaysFragment.newInstance());
            t.replace(R.id.liste_fragment_frame, u);
            t.addToBackStack(null);
            //t.addToBackStack(null);FragmentTransaction t;
            t.commit();
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
