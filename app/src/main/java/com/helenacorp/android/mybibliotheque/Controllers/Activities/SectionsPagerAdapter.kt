package com.helenacorp.android.mybibliotheque.Controllers.Activities

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AccountFragment
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AddBookFragment
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.ListBooksFragment
import com.helenacorp.android.mybibliotheque.R

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0->return AccountFragment.newInstance(position +1)
            1->return AddBookFragment.newInstance(position+1)
            2->return ListBooksFragment.newInstance(position+1)
        }

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return AccountFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 3
    }

}