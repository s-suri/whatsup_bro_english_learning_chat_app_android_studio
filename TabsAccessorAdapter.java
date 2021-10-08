package com.some.notes;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter
{

    public TabsAccessorAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {


        switch (i)
        {

            case 0:
                ChatsFragment chatFragment = new ChatsFragment();
                return chatFragment;

            case 1:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;

            case 2:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;


            default:
                return null;
        }
    }


    @Override
    public int getCount()
    {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {


        switch (position) {
            case 0:

                    return "Chats";

            case 1:

                return "Group";



            case 2:

                return "Requests";

            default:
                return null;
        }

    }
}
