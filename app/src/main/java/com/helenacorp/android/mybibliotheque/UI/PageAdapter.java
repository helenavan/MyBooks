package com.helenacorp.android.mybibliotheque.UI;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AccountFragment;
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AddBookFragment;
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.ListBooksFragment;

public class PageAdapter extends FragmentPagerAdapter {

    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: //Page number 1
                return AccountFragment.newInstance();
            case 1: //Page number 2
                return AddBookFragment.newInstance();
            case 2: //Page number 3
                return ListBooksFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: //Page number 1
                return "Profil";
            case 1: //Page number 2
                return "Ajouter";
            case 2: //Page number 3
                return "Liste";
            default:
                return null;
        }
    }
}
