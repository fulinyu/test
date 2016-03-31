package flyu.comparetool;
import android.content.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class MyAdapter extends BaseAdapter
{
	int[] positions;
	Context context;
	int color;
	File[] title;
	public MyAdapter(Context c, int [] p1, int color,File[]title)
	{
		context = c;
		positions = p1;
		this.color = color;
		this.title=title;
	}
	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return title.length;
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return p1;
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		TextView mTextView=new TextView(context);
		
		for(int i=0;i<positions.length;i++){
			if(positions[i]!=p1){
			mTextView.setTextColor(color);
				
			}
		}
		mTextView.setText(title[p1].getName());
		
		return mTextView;
	}

}
