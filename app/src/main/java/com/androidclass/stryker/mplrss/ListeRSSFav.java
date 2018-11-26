package com.androidclass.stryker.mplrss;

import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListeRSSFav.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListeRSSFav#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListeRSSFav extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String authority = "fr.cartman.respect.my.authority";

    private OnFragmentInteractionListener mListener;

    private SimpleCursorAdapter adapter;

    private ContentResolver contentResolver;


    public final static int VERSION = 9;
    public final static String DB_NAME = "base_rss";
    public final static String TABLE_RSS = "rss";
    public final static String COLONNE_TITLE = "title";
    public final static String COLONNE_LINK = "link";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_CHOISI = "choisi";


    public ListeRSSFav() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListeRSSFav.
     */
    // TODO: Rename and change types and number of parameters
    public static ListeRSSFav newInstance() {
        return new ListeRSSFav();
    }

    public void setContentResolver(Context context){
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onAttach(getActivity());
        if (getActivity() instanceof ListeRSS.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getActivity();
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
        ContentValues cv = new ContentValues();
        cv.put("date_choisi", "");
        cv.put("choisi", 0);
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("rss").appendPath("date_choisi").build();
        contentResolver.update(uri,cv, "title = ?", new String [] {nom});
        mListener.onRSSSelectionFav(nom);
    }
    /*

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste_rssfav, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    */

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
        return new CursorLoader(getActivity(), uri, new String[] {"rowid as _id", "title"}, "choisi = 1", null, null);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onRSSSelectionFav(String title);
    }
}
