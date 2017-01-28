package com.offersky.nomad.hitchbeacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class ThreeFragment extends Fragment {

    RecyclerView recyclerView;
    //FloatingActionButton fab;

    CouponAdapter adapter;
    List<Note> coupons = new ArrayList<>();
    //List<Note> couponsToShow = new ArrayList<>();
    String TAG = "coupons";

    long initialCount;

    int modifyPos = -1;
    private DatabaseReference mDatabase;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_one, container, false);
        View view = inflater.inflate(R.layout.activity_main,container,false);
        Log.d(TAG, "onCreate");
        mySwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_coupons);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(gridLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            Log.d(TAG, "downloading coupons");
            coupons = new ArrayList<>(Hitchbeacon.noteLinkedHashMap.values());
            for(Note c:coupons){
                Log.i("Getting coupons", c.getNote()+ " note");
            }
            initialCount = coupons.size();
            Log.d(TAG, initialCount + " size of coupons recieved");

        } catch (Exception e) {
            Log.d(TAG, "error in downloading coupons");
            initialCount = 0;
            e.printStackTrace();
        }

            /*try {
                coupons = new ArrayList<>(Hitchbeacon.noteLinkedHashMap.values());
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        /*for(Note c:coupons){
            if(Hitchbeacon.user.discoveredNotes.contains(c.getNote())){
                couponsToShow.add(c);
            }

        }*/

        for(Note c:coupons){
            Log.i("Getting coupons", c.getNote()+ " note");
        }
            adapter = new CouponAdapter(getActivity(), coupons);
//            updateOffers();
            recyclerView.setAdapter(adapter);


       /* ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/
        try {
            adapter.SetOnItemClickListener(new CouponAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    Log.d(TAG, "click");
                    Intent i = new Intent(getContext(), DetailedActivity.class);
                    i.putExtra("isEditing", true);
                    i.putExtra("title", coupons.get(position).title);
                    i.putExtra("note", coupons.get(position).note);
                    i.putExtra("URL", coupons.get(position).getShopURI());
                    i.putExtra("code",coupons.get(position).getCode());
                    i.putExtra("logoURL",coupons.get(position).getLogoURI());
                    modifyPos = position;

                    startActivity(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mySwipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
//
//                        // This method performs the actual data-refresh operation.
//                        // The method calls setRefreshing(false) when it's finished.
//                        List<Note> allOffers = new ArrayList<>();
//                        allOffers = new ArrayList<>(Hitchbeacon.noteLinkedHashMap.values());
//                        if(allOffers.size() != 0){
//                            coupons.clear();
//                        }
//                        for(Note offer : allOffers){
//                                coupons.add(offer);
//
//                        }
//                    }
//                }
//        );
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("coupons"));
        return view;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            updateOffers();
            Log.d("receiver", "Got Broadcast for notes");
        }
    };

//    public void updateOffers(){
//        try {
//            List<Note> allOffers;// = new ArrayList<>();
//            allOffers = new ArrayList<>(Hitchbeacon.noteLinkedHashMap.values());
//            if(allOffers.size() != 0){
//                couponsToShow.clear();
//            }
//            for(Note offer : allOffers){
//
//                if (Hitchbeacon.user.discoveredNotes.contains(offer.getNote())) {
//                    couponsToShow.add(offer);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG,"updateOffers called.");
//        adapter.notifyDataSetChanged();
//    }

    public void updateOffers(){
        List<Note> allOffers;
        allOffers = new ArrayList<>(Hitchbeacon.noteLinkedHashMap.values());
        coupons.clear();
//        Random r = new Random();
//        int Low = 0;
        int High = allOffers.size();
//        for (int i  = 0; i < 5 ; i ++){
//            int Result = r.nextInt(High-Low) + Low;
//            couponsToShow.add(allOffers.get(Result));
//        }
//        for (Offer offer:allOffers){
//            offers.add(offer);
//        }
        /*final Random random = new Random();
        final Set<Integer> intSet = new HashSet<>();
        while (intSet.size() < 5) {
            intSet.add(random.nextInt(High));
        }
        final int[] ints = new int[intSet.size()];
        final Iterator<Integer> iter = intSet.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            ints[i] = iter.next();
        }*/
        for (Note note:allOffers){
            coupons.add(note);
        }
        Log.d("note","update notes called actually.");
        adapter.notifyDataSetChanged();
    }




    // Handling swipe to delete
    /*ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //Remove swiped item from list and notify the RecyclerView

//            final int position = viewHolder.getAdapterPosition();
//            final Note note = coupons.get(viewHolder.getAdapterPosition());
//            coupons.remove(viewHolder.getAdapterPosition());
//            adapter.notifyItemRemoved(position);
//            initialCount -= 1;
////            mDatabase.child("coupons").child(note.getUid()).setValue(null);
//
//
//            Snackbar.make(recyclerView, "Deals deleted", Snackbar.LENGTH_SHORT)
//                    .setAction("UNDO", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
////                                note.save();
//                            coupons.add(position, note);
//                            adapter.notifyItemInserted(position);
//                            initialCount += 1;
//
//                        }
//                    })
//                    .show();
        }*/





}
