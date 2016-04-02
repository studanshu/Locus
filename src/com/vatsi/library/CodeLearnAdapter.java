package com.vatsi.library;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.vatsi.locus.*;
import com.vatsi.library.DatabaseHandler;

public class CodeLearnAdapter extends BaseAdapter {
		DatabaseHandler dh;
		List<ListObject> codeLearnChapterList;
	    public CodeLearnAdapter(DatabaseHandler dh) {
			super();
			this.dh = dh;
			codeLearnChapterList = dh.getDataForListView();
	    }
	    
	    @Override
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return codeLearnChapterList.size();
	    }

	    @Override
	    public ListObject getItem(int arg0) {
	        // TODO Auto-generated method stub
	    	
	        return codeLearnChapterList.get(arg0);
	    }

	    @Override
	    public long getItemId(int arg0) {
	        return arg0;
	    }

	    @Override
	    public View getView(int arg0, View arg1, ViewGroup arg2) {

	        if(arg1==null)
	        {
	        	
	        	LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(arg2.getContext());
	            arg1 = inflater.inflate(R.layout.listitem, arg2,false);
	        }

	        TextView chapterName = (TextView)arg1.findViewById(R.id.textView1);
	        TextView chapterDate = (TextView)arg1.findViewById(R.id.textView2);
	        ImageView chapterImage = (ImageView)arg1.findViewById(R.id.imageView1);
 	        ListObject chapter = codeLearnChapterList.get(arg0);
 	        Bitmap bmp = BitmapFactory.decodeByteArray(chapter.getObjectPicture(), 0, chapter.getObjectPicture().length);
	        chapterName.setText(chapter.getObjectName());
	        chapterDate.setText(chapter.getDateAndTime());
	        chapterImage.setImageBitmap(bmp);
	        return arg1;
	    }
	}