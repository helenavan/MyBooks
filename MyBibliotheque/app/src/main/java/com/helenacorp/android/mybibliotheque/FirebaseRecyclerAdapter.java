package com.helenacorp.android.mybibliotheque;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;


public abstract class FirebaseRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder, BookModel> extends RecyclerView.Adapter<ViewHolder> implements Filterable {

    private Query mQuery;
    private int mLayout;
    private Class<BookModel> mModelClass;
    private ArrayList<BookModel> mItems;
    private ArrayList<BookModel> mItemsCopy;
    private ArrayList<String> mKeys;
    private LayoutInflater mInflater;
    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();

                BookModel item = dataSnapshot.getValue(FirebaseRecyclerAdapter.this.mModelClass);
                // BookModel item = getConvertedObject(dataSnapshot);
                int insertedPosition;
                if (previousChildName == null) {
                    mItems.add(0, item);
                    mKeys.add(0, key);
                    insertedPosition = 0;
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mItems.size()) {
                        mItems.add(item);
                        mKeys.add(key);
                    } else {
                        mItems.add(nextIndex, item);
                        mKeys.add(nextIndex, key);
                    }
                    insertedPosition = nextIndex;
                }
                notifyItemInserted(insertedPosition);
                itemAdded(item, key, insertedPosition);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();

            if (mKeys.contains(key)) {
                int index = mKeys.indexOf(key);
                BookModel oldItem = mItems.get(index);
                BookModel newItem = getConvertedObject(dataSnapshot);

                mItems.set(index, newItem);

                notifyItemChanged(index);
                itemChanged(oldItem, newItem, key, index);
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();

            if (mKeys.contains(key)) {
                int index = mKeys.indexOf(key);
                BookModel item = mItems.get(index);

                mKeys.remove(index);
                mItems.remove(index);

                notifyItemRemoved(index);
                itemRemoved(item, key, index);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();

            int index = mKeys.indexOf(key);
            BookModel item = getConvertedObject(dataSnapshot);
            mItems.remove(index);
            mKeys.remove(index);
            int newPosition;
            if (previousChildName == null) {
                mItems.add(0, item);
                mKeys.add(0, key);
                newPosition = 0;
            } else {
                int previousIndex = mKeys.indexOf(previousChildName);
                int nextIndex = previousIndex + 1;
                if (nextIndex == mItems.size()) {
                    mItems.add(item);
                    mKeys.add(key);
                } else {
                    mItems.add(nextIndex, item);
                    mKeys.add(nextIndex, key);
                }
                newPosition = nextIndex;
            }
            notifyItemMoved(index, newPosition);
            itemMoved(item, key, index, newPosition);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur.");
        }

    };

    public FirebaseRecyclerAdapter(Query mQuery, Class<BookModel> mModelClass, int mLayout, Activity activity) {
        this.mQuery = mQuery;
        this.mModelClass = mModelClass;
        this.mLayout = mLayout;
        mInflater = activity.getLayoutInflater();
        mItems = new ArrayList<BookModel>();
        mItemsCopy = new ArrayList<BookModel>();
        mKeys = new ArrayList<String>();

        mQuery.addChildEventListener(mListener);

        /**
         * @param query The Firebase location to watch for data changes.
         *              Can also be a slice of a location, using some combination of
         *              <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
         * @param items List of items that will load the adapter before starting the listener.
         *              Generally null or empty, but this can be useful when dealing with a
         *              configuration change (e.g.: reloading the adapter after a device rotation).
         *              Be careful: keys must be coherent with this list.
         * @param keys  List of keys of items that will load the adapter before starting the listener.
         *              Generally null or empty, but this can be useful when dealing with a
         *              configuration change (e.g.: reloading the adapter after a device rotation).
         *              Be careful: items must be coherent with this list.
         */
    }



  /*  public FirebaseRecyclerAdapter(Query mQuery,
                                   @Nullable ArrayList<BookModel> items,
                                   @Nullable ArrayList<String> keys) {
        this.mQuery = mQuery;
        if (items != null && keys != null) {
            this.mItems = items;
            this.mKeys = keys;
        } else {
            mItems = new ArrayList<BookModel>();
            mKeys = new ArrayList<String>();
        }
        mQuery.addChildEventListener(mListener);
    }*/

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, final int position);

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    /**
     * Clean the adapter.
     * ALWAYS call this method before destroying the adapter to remove the listener.
     */
    public void destroy() {
        mQuery.removeEventListener(mListener);
        mItems.clear();
    }

    /**
     * Returns the list of items of the adapter: can be useful when dealing with a configuration
     * change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of items of the adapter
     */
    public ArrayList<BookModel> getItems() {
        return mItems;
    }

    /**
     * Returns the list of keys of the items of the adapter: can be useful when dealing with a
     * configuration change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of keys of the items of the adapter
     */
    public ArrayList<String> getKeys() {
        return mKeys;
    }

    /**
     * Returns the item in the specified position
     *
     * @param position Position of the item in the adapter
     * @return the item
     */
    public BookModel getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Returns the position of the item in the adapter
     *
     * @param item Item to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    public int getPositionForItem(BookModel item) {
        return mItems != null && mItems.size() > 0 ? mItems.indexOf(item) : -1;
    }

    /**
     * Check if the searched item is in the adapter
     *
     * @param item Item to be searched
     * @return true if the item is in the adapter, false otherwise
     */
    public boolean contains(BookModel item) {
        return mItems != null && mItems.contains(item);
    }

    /**
     * ABSTRACT METHODS THAT MUST BE IMPLEMENTED BY THE EXTENDING ADAPTER.
     */

    /**
     * Called after an item has been added to the adapter
     *
     * @param item     Added item
     * @param key      Key of the added item
     * @param position Position of the added item in the adapter
     */
    protected void itemAdded(BookModel item, String key, int position) {

    }

    /**
     * Called after an item changed
     *
     * @param oldItem  Old version of the changed item
     * @param newItem  Current version of the changed item
     * @param key      Key of the changed item
     * @param position Position of the changed item in the adapter
     */
    protected void itemChanged(BookModel oldItem, BookModel newItem, String key, int position) {

    }

    /**
     * Called after an item has been removed from the adapter
     *
     * @param item     Removed item
     * @param key      Key of the removed item
     * @param position Position of the removed item in the adapter
     */
    protected void itemRemoved(BookModel item, String key, int position) {

    }

    /**
     * Called after an item changed position
     *
     * @param item        Moved item
     * @param key         Key of the moved item
     * @param oldPosition Old position of the changed item in the adapter
     * @param newPosition New position of the changed item in the adapter
     */
    protected void itemMoved(BookModel item, String key, int oldPosition, int newPosition) {

    }

    /**
     * Converts the data snapshot to generic object
     *
     * @param snapshot Result
     * @return Data converted
     */
    protected BookModel getConvertedObject(DataSnapshot snapshot) {
        return snapshot.getValue(getGenericClass());
    }

    /**
     * Returns a class reference from generic T.
     */
    @SuppressWarnings("unchecked")
    private Class<BookModel> getGenericClass() {
        return (Class<BookModel>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mItemsCopy = mItems;
                } else {

                    ArrayList<BookModel> filteredList = new ArrayList<>();

                    for (BookModel androidVersion : mItems) {

                        if (androidVersion.toString().toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }

                    mItemsCopy = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mItemsCopy;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mItemsCopy = (ArrayList<BookModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
