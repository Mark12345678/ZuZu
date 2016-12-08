package cn.liaojh.zuzu.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.liaojh.zuzu.R;


/**
 * Created by Liaojh on 2016/11/26.
 */

public class ChatListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list,container,false);
        return view;
    }

}
