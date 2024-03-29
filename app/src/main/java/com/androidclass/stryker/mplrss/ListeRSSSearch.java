package com.androidclass.stryker.mplrss;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UnRSS#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListeRSSSearch extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "titleRSS";

    private String titleRSS;

    private ListeRSSSearch.OnFragmentInteractionListener mListener;

    private SimpleCursorAdapter adapter;

    private ContentResolver contentResolver;

    private String authority = "fr.cartman.respect.my.authority";


    private TextView title;

    public ListeRSSSearch() {
        // Required empty public constructor
    }

    public void setContentResolver(Context context){
        contentResolver = context.getContentResolver();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nomPays nom du pays.
     * @return A new instance of fragment ListRSSSearch.
     */
    public static ListeRSSSearch newInstance(String nomPays) {
        ListeRSSSearch fragment = new ListeRSSSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, nomPays);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleRSS= getArguments().getString(ARG_PARAM1);

        }

        super.onAttach(getActivity());
        if (getActivity() instanceof ListeRSSSearch.OnFragmentInteractionListener) {
            mListener = (ListeRSSSearch.OnFragmentInteractionListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[] {"title"}, new int[] {android.R.id.text1});
        setListAdapter(adapter);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Cursor c = (Cursor) getListAdapter().getItem(position);
        String nom = c.getString(c.getColumnIndex("title"));
        mListener.onRSSSelection(nom);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("rss").build();
        return new CursorLoader(getActivity(), uri, new String[] {"rowid as _id", "title"}, "title LIKE ? OR description LIKE ?", new String [] {"%"+titleRSS+"%"}, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
        adapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.@Override
     public void onRSSSelection(String title) {
     UnRSS u = UnRSS.newInstance(title);
     u.setContentResolver(MainActivity.this);
     FragmentTransaction t = f.beginTransaction();
     //t.replace(R.id.liste_fragment_frame, ListPaysFragment.newInstance());
     t.replace(R.id.liste_fragment_frame, u);
     t.addToBackStack(null);
     //t.addToBackStack(null);FragmentTransaction t;
     t.commit();
     }
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void onRSSSelection(String title);
    }
}

