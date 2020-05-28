package com.helenacorp.android.mybibliotheque.UI

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AccountFragment.Companion.newInstance
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AddBookFragment.Companion.newInstance
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.ListBooksFragment.Companion.newInstance
import androidx.fragment.app.FragmentPagerAdapter
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AccountFragment
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AddBookFragment
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.ListBooksFragment

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AccountFragment.newInstance()
            1 -> AddBookFragment.newInstance()
            2 -> ListBooksFragment.newInstance()
            else -> null
        }!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Profil"
            1 -> "Ajouter"
            2 -> "Liste"
            else -> null
        }
    }
}