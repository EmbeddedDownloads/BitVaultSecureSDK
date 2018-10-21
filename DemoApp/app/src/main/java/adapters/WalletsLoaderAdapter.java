package adapters;/**
 * Created by ${e} on 5/24/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.embedded.sdkdemo.R;

import java.util.ArrayList;

import model.WalletDetails;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class WalletsLoaderAdapter extends BaseAdapter {
    private ArrayList<WalletDetails> mRequestedWallets = null;
    private Activity mActivity = null;
    private LayoutInflater layoutInflater = null;

    public WalletsLoaderAdapter(Activity mActivity, ArrayList<WalletDetails> mRequestedWallets) {
        this.mRequestedWallets = mRequestedWallets;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mRequestedWallets != null)
            count = mRequestedWallets.size();
        return count;
    }

    @Override
    public Object getItem(int position) {
        Object mObject = null;
        if (mRequestedWallets != null)
            mObject = mRequestedWallets.get(position);
        return mObject;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewholder viewholder = null;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.spinner_loader, null, false);
            viewholder = new Viewholder();
            viewholder.wallet_name = (TextView) convertView.findViewById(R.id.wallet_name);

            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }
        if (mRequestedWallets != null) {
            String walletName = mRequestedWallets.get(position).getmKeyPair().address;
            viewholder.wallet_name.setText(walletName);
        }
        return convertView;
    }

    /**
     * View holder class to hold the connected cameras view
     */
    private class Viewholder {

        /**
         * text view which hold the name of the camera.
         */
        TextView wallet_name;

    }
}
