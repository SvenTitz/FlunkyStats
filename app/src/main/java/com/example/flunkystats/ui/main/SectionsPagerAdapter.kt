package com.example.flunkystats.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.flunkystats.R

private val TAB_TITLES = arrayOf(
    R.string.tab_title_hits,
    R.string.tab_title_slugs,
    R.string.tab_title_matches,
    R.string.tab_title_tourn
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return when(position) {
            0 -> RankingFragment.newInstance(RankingFragment.STAT_TYPE_HITS)
            1 -> RankingFragment.newInstance(RankingFragment.STAT_TYPE_SLUGS)
            2 -> RankingFragment.newInstance(RankingFragment.STAT_TYPE_MATCHES)
            3 -> RankingFragment.newInstance(RankingFragment.STAT_TYPE_TOURN)
            else -> RankingFragment.newInstance(RankingFragment.STAT_TYPE_ERROR)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 4
    }
}