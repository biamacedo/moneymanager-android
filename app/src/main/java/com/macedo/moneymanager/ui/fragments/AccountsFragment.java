package com.macedo.moneymanager.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.AccountItemListAdapter;
import com.macedo.moneymanager.database.AccountsDatasource;
import com.macedo.moneymanager.models.Account;
import com.macedo.moneymanager.ui.EditAccountActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountsFragment extends Fragment {

    private ListView mListView;
    private TextView mTotalAmountTextView;

    private ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();
    private ArrayList<Account> mAccounts;

    private AccountItemListAdapter mAdapter;
    private Vibrator mVib;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountsFragment.
     */
    public static AccountsFragment newInstance() {
        AccountsFragment fragment = new AccountsFragment();
        //Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return fragment;
    }

    public AccountsFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);

        mTotalAmountTextView = (TextView) rootView.findViewById(R.id.totalAmount);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                Intent intent = new Intent(getActivity(), EditAccountActivity.class);
                Account clickedAccount = mAccounts.get(position);
                intent.putExtra(Account.ACCOUNT_EXTRA, clickedAccount);
                startActivity(intent);
            }
        });

        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                mVib.vibrate(50);
                final int checkedCount = mListView.getCheckedItemCount();
                switch (checkedCount) {
                    case 0:
                        mode.setSubtitle(null);
                        break;
                    case 1:
                        mode.setSubtitle("One account selected");
                        break;
                    default:
                        mode.setSubtitle(checkedCount + " accounts selected");
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
                mode.setTitle("Select Accounts");
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
                            AccountsDatasource datasource = new AccountsDatasource(getActivity());
                            for (Integer i : mSelectedItems) {
                                datasource.delete(mAccounts.get(i).getId());
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
        AccountsDatasource datasource = new AccountsDatasource(getActivity());
        mAccounts = datasource.read();
        mAdapter = new AccountItemListAdapter(getActivity(), mAccounts, mSelectedItems);
        mListView.setAdapter(mAdapter);
        mTotalAmountTextView.setText("$" + String.format("%.2f", datasource.sumAllAccounts()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_list, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new) {
            Intent intent = new Intent(getActivity(), EditAccountActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
