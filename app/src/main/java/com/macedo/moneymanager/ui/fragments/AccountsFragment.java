package com.macedo.moneymanager.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.adapters.AccountItemListAdapter;
import com.macedo.moneymanager.database.AccountsDatasource;
import com.macedo.moneymanager.database.ExpensesDatasource;
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

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ACCOUNT_EXTRA = "ACCOUNT";

    public ListView mListView;
    public TextView mTotalAmountTextView;

    public ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();
    public ArrayList<Account> mAccounts;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountsFragment.
     */
    // TODO: Rename and change types and number of parameters
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);

        mListView = (ListView) rootView.findViewById(R.id.accountListView);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //mListView.setMultiChoiceModeListener();
        mTotalAmountTextView = (TextView) rootView.findViewById(R.id.totalAmount);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditAccountActivity.class);
                Account clickedAccount = mAccounts.get(position);
                intent.putExtra(ACCOUNT_EXTRA, clickedAccount);
                startActivity(intent);
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                    mSelectedItems.add(position);
                } else {
                    view.setSelected(false);
                    mSelectedItems.remove(Integer.valueOf(position));
                }
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshList();
    }

    public void refreshList(){
        AccountsDatasource datasource = new AccountsDatasource(getActivity());
        mAccounts = datasource.read();
        mListView.setAdapter(new AccountItemListAdapter(getActivity(), mAccounts));
        mTotalAmountTextView.setText("$" + String.format("%.2f", datasource.sumAllAccounts()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_account_list_fragment, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_account) {
            Intent intent = new Intent(getActivity(), EditAccountActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete_expense) {
            if (mSelectedItems.size() > 0){
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete " + mSelectedItems.size() + " item(s)?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                ExpensesDatasource datasource = new ExpensesDatasource(getActivity());
                                for(Integer i : mSelectedItems){
                                    datasource.delete(mAccounts.get(i).getId());
                                }
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
