package com.applozic.mobicomkit.uiwidgets.conversation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicommons.file.FileUtils;

import java.util.ArrayList;


public class MobiComAttachmentGridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Uri> uris;

    public MobiComAttachmentGridViewAdapter(Context context, ArrayList<Uri> uris) {
        this.context = context;
        this.uris = uris;
    }

    @Override
    public int getCount() {
        //Extra one item is added
        return uris.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return uris.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null){
            view = inflater.inflate(R.layout.mobicom_attachment_gridview_item, viewGroup, false);//Inflate layout
        }

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.mobicom_attachment_delete_btn);
        final ImageView imageView = (ImageView) view.findViewById(R.id.galleryImageView);
        final TextView fileName  = (TextView) view.findViewById(R.id.mobicom_attachment_file_name);

        //Last element
        if (position == getCount()-1) {

            deleteButton.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.mobicom_new_attachment);
            fileName.setVisibility(View.VISIBLE);
            fileName.setText("Tap to attach file");

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent getContentIntent = FileUtils.createGetContentIntent();
                    getContentIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    Intent intentPick = Intent.createChooser(getContentIntent, context.getString(R.string.select_file));
                    ((Activity) context).startActivityForResult(intentPick, 100);
                }
            });
            return view;
        }else{
            deleteButton.setVisibility(View.VISIBLE);

        }

        Uri uri = (Uri)getItem(position);

        Bitmap previewBitmap = getPreview(uri) ;
        if(previewBitmap!=null){
            imageView.setImageBitmap(previewBitmap);
        }else{
            imageView.setImageResource(R.drawable.mobicom_attachment_file);
            fileName.setVisibility(View.VISIBLE);
            fileName.setText(getFileName(uri));
        }
        fileName.setText(getFileName(uri));

        deleteButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uris.remove(position);
                        notifyDataSetChanged();
                    }
                });
        return view;
    }


    /**
     *
     * @param uri
     * @return
     */
    Bitmap getPreview(Uri uri) {
        String filePath  =FileUtils.getPath(context, uri);

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / 200;
        return BitmapFactory.decodeFile(filePath, opts);
    }

    /**
     *
     * @param uri
     * @return
     */
    public String getFileName(Uri uri) {

        String fileName=null;
        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
        if (returnCursor != null &&  returnCursor.moveToFirst()) {
           int columnIndex =  returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            fileName=  returnCursor.getString(columnIndex);
        }

        return fileName;
    }

}