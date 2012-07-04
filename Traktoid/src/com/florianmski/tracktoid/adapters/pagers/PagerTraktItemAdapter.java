package com.florianmski.tracktoid.adapters.pagers;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.ui.fragments.traktitems.PI_TraktItemEpisodeFragment;
import com.florianmski.tracktoid.ui.fragments.traktitems.PI_TraktItemMovieFragment;
import com.florianmski.tracktoid.ui.fragments.traktitems.PI_TraktItemShowFragment;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class PagerTraktItemAdapter<T extends TraktoidInterface<T>> extends FragmentStatePagerAdapter
{
	private List<T> items;

	@SuppressWarnings("unchecked")
	public PagerTraktItemAdapter(List<T> items, FragmentManager fm, Context context)
	{
		super(fm);

		DatabaseWrapper dbw = new DatabaseWrapper(context);

		//if a show on this list is in the db, get infos so we can display them (watched, loved...)
		for(int i = 0; i < items.size(); i++)
		{
			T item = items.get(i);
			if(dbw.showExist(item.getId()))
			{
				if(item instanceof TvShow)
					items.set(i, (T) dbw.getShow(item.getId()));
				else if(item instanceof Movie)
					items.set(i, (T) dbw.getMovie(((Movie) item).url));
			}
		}

		dbw.close();

		this.items = items;		
	}

	public void clear() 
	{
		items.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() 
	{
		return items.size();
	}

	@Override
	public Fragment getItem(int position) 
	{
		if(items.get(position) instanceof TvShow)
			return PI_TraktItemShowFragment.newInstance((TvShow)items.get(position));
		else if(items.get(position) instanceof Movie)
			return PI_TraktItemMovieFragment.newInstance((Movie)items.get(position));
		else if(items.get(position) instanceof TvShowEpisode)
			return PI_TraktItemEpisodeFragment.newInstance((TvShowEpisode)items.get(position));
		else
			return null;
	}
	
	public T getTraktItem(int position)
	{
		return items.get(position);
	}

	@Override
	/** @see http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view */
	public int getItemPosition(Object object) 
	{
		return POSITION_NONE;
	}

	public boolean isEmpty() 
	{
		return getCount() == 0;
	}
}