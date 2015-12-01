package com.macedo.moneymanager.ui.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.DailyOperationItemListAdapter;
import com.macedo.moneymanager.database.OperationsDatasource;
import com.macedo.moneymanager.models.Operation;
import com.macedo.moneymanager.ui.EditOperationActivity;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class DailyOperationsFragment extends Fragment {

    private static final String TAG = DailyOperationsFragment.class.getSimpleName();

    private ExpandableStickyListHeadersListView mExpandableStickyList;
    private TextView mTotalAmountTextView;

    private ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();
    private ArrayList<Operation> mOperations;

    private DailyOperationItemListAdapter mAdapter;

    private Vibrator mVib;

    public static DailyOperationsFragment newInstance() {
        return new DailyOperationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mVib = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_operations, container, false);

        mExpandableStickyList = (ExpandableStickyListHeadersListView) rootView.findViewById(R.id.listView);
        mTotalAmountTextView = (TextView) rootView.findViewById(R.id.totalAmount);

        mExpandableStickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (mExpandableStickyList.isHeaderCollapsed(headerId)) {
                    mExpandableStickyList.expand(headerId);
                } else {
                    mExpandableStickyList.collapse(headerId);
                }
            }
        });

        mExpandableStickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                Intent intent = new Intent(getActivity(), EditOperationActivity.class);
                Operation clickedOperation = mOperations.get(position);
                intent.putExtra(Operation.OPERATION_EXTRA, clickedOperation);
                startActivity(intent);
            }
        });

        mExpandableStickyList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mExpandableStickyList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                mVib.vibrate(50);
                final int checkedCount = mExpandableStickyList.getCheckedItemCount();
                switch (checkedCount) {
                    case 0:
                        mode.setSubtitle(null);
                        break;
                    case 1:
                        mode.setSubtitle("One expense selected");
                        break;
                    default:
                        mode.setSubtitle(checkedCount + " expenses selected");
                        break;
                }

                if (checked) {
                    mSelectedItems.add(position);
                } else {
                    mSelectedItems.remove(Integer.valueOf(position));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deleteSelectedItems();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_list_context, menu);
                mode.setTitle("Select Expenses");
                mAdapter.setSelectMode(true);
                mAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                mAdapter.setSelectMode(false);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    private void deleteSelectedItems(){
        if (mSelectedItems.size() > 0) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete " + mSelectedItems.size() + " item(s)?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            OperationsDatasource datasource = new OperationsDatasource(getActivity());
                            for(Integer i : mSelectedItems){
                                datasource.delete(mOperations.get(i).getId());
                                refreshList();
                            }
                            mSelectedItems.clear();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_exclamation_triangle)
                    .show();
            refreshList();
        } else {
            Toast.makeText(getActivity(), "No item selected.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshList();
    }

    public void refreshList(){
        OperationsDatasource datasource = new OperationsDatasource(getActivity());
        mOperations = datasource.read();
        mAdapter = new DailyOperationItemListAdapter(getActivity(), mOperations, mSelectedItems);
        mExpandableStickyList.setAdapter(mAdapter);
        mTotalAmountTextView.setText("$" + String.format("%.2f", datasource.sumAllExpenses()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_list, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {
            Intent intent = new Intent(getActivity(), EditOperationActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
