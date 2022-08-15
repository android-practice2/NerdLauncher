package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {
    private static final String TAG = "NerdLauncherFragment";
    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        mRecyclerView = layout.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        return layout;
    }

    private void setupAdapter() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        Collections.sort(resolveInfos, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.loadLabel(packageManager).toString()
                        , o2.loadLabel(packageManager).toString());
            }
        });

        mRecyclerView.setAdapter(new ActivityAdapter(resolveInfos));

        Log.i(TAG, "found " + resolveInfos.size() + " activities");

    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextView;
        private ImageView mAppItemImage;
        private ResolveInfo mResolveInfo;

        public ActivityHolder(@NonNull View itemView) {
            super(itemView);
            TextView app_item_text = itemView.findViewById(R.id.app_item_text);
            ImageView app_item_image = itemView.findViewById(R.id.app_item_image);

            mTextView = app_item_text;
            mAppItemImage=app_item_image;

            mTextView.setOnClickListener(this);
            mAppItemImage.setOnClickListener(this);

        }



        public void bind(ResolveInfo resolveInfo) {
            this.mResolveInfo = resolveInfo;
            PackageManager packageManager = getActivity().getPackageManager();
            mTextView.setText(resolveInfo.loadLabel(packageManager).toString());
            Drawable drawable = resolveInfo.loadIcon(packageManager);

            mAppItemImage.setImageDrawable(drawable);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(mResolveInfo.activityInfo.applicationInfo.packageName,
                    mResolveInfo.activityInfo.name
            );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private List<ResolveInfo> resolveInfos;

        public ActivityAdapter(List<ResolveInfo> resolveInfos) {
            this.resolveInfos = resolveInfos;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            TextView textView =(TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            View layout = inflater.inflate(R.layout.componant_app, parent, false);


            return new ActivityHolder(layout);
//            return new ActivityHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            holder.bind( resolveInfos.get(position));

        }

        @Override
        public int getItemCount() {
            return resolveInfos.size();
        }
    }

}
