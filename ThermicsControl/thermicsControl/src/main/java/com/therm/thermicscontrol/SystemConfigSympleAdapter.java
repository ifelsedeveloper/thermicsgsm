package com.therm.thermicscontrol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SystemConfigSympleAdapter extends SimpleCursorAdapter implements
        OnClickListener, OnLongClickListener {
    Dialog dialog = null;
    private View oldSelectedView;
    private Context context = null;
    private Cursor cursor = null;
    private ListView listView = null;
    private int nSelectedItem = 0;
    private int longClickPosition = 0;
    private SystemConfigDataSource systemConfigDataSource = null;

    public SystemConfigSympleAdapter(final Context context, int layout, Cursor c,
                                     String[] from, int[] to, int flags, ListView listView, SystemConfigDataSource systemConfig_DB) {
        super(context, layout, c, from, to, flags);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.cursor = c;
        this.listView = listView;
        this.listView.setAdapter(this);
        this.systemConfigDataSource = systemConfig_DB;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_change_delete);
        dialog.setTitle("Выберите действие");
        nSelectedItem = systemConfigDataSource.getSelectedPosition();
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonChange);
        dialogButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //perform action
                changeRequest(longClickPosition);
                dialog.dismiss();
            }
        });

        dialogButton = (Button) dialog.findViewById(R.id.dialogButtonDelete);
        dialogButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //perform action
                deleteRequest(longClickPosition);
                dialog.dismiss();
            }
        });

        dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        dialogButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //perform action
                dialog.dismiss();
            }
        });
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        this.cursor.moveToPosition(position);
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.system_row, parent, false);
        }
        TextView textView = (TextView) rowView.findViewById(R.id.textViewSystemName);

        String systemName = this.cursor.getString(this.cursor.getColumnIndex(SystemConfigSQLiteHelper.COLUMN_NAME));
        textView.setText(systemName);

        if (nSelectedItem == position) {
            rowView.setBackgroundColor(context.getResources().getColor(R.color.pressed_color_system));
        } else {
            rowView.setBackgroundColor(context.getResources().getColor(R.color.default_color_system));
        }

        View imageView = rowView.findViewById(R.id.checkSystemImage);
        ImageView checkMark = (ImageView) imageView;
        if (nSelectedItem == position) {
            checkMark.setImageDrawable(context.getResources().getDrawable(R.drawable.check_mark));
        } else {
            checkMark.setImageDrawable(null);
        }
        rowView.setTag(position);
        rowView.setOnLongClickListener(this);
        rowView.setOnClickListener(this);
        return rowView;
    }

    @Override
    public boolean onLongClick(View view) {
        int position = (Integer) view.getTag();
        longClickPosition = position;
        dialog.show();
        return true;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        if (oldSelectedView != null) {
            oldSelectedView.setBackgroundColor(context.getResources().getColor(R.color.default_color_system));
            ImageView checkMark = (ImageView) oldSelectedView.findViewById(R.id.checkSystemImage);
            checkMark.setImageDrawable(null);
        } else
            notifyDataSetChanged();
        nSelectedItem = position;
        view.setBackgroundColor(context.getResources().getColor(R.color.pressed_color_system));
        oldSelectedView = view;
        ImageView checkMark = (ImageView) oldSelectedView.findViewById(R.id.checkSystemImage);
        checkMark.setImageDrawable(context.getResources().getDrawable(R.drawable.check_mark));
        systemConfigDataSource.setSelectedSystem(getIdSystemFrom(position));
        //notify by using Broadcast
        Intent intent = new Intent(SystemConfigDataSource.SYSTEM_SELECTION_CHANGED);
        context.sendBroadcast(intent);
    }

    private void changeRequest(int position) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Объект");
        //alert.setMessage("Message");
        final EditText input = new EditText(context);
        final int id_system = getIdSystemFrom(position);
        SystemConfig systemConfig = systemConfigDataSource.getSystemConfig(id_system);
        final String name_system = systemConfig.getName();
        input.setText(name_system);

        alert.setView(input);

        input.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(input, 0);
                input.setSelection(name_system.length());
            }
        }, 120);

        alert.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                systemConfigDataSource.updateSystemConfigName(id_system, value);
                refreshListView();
            }
        });

        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    private void deleteRequest(int position) {
        if (getIdSystemFrom(position) != 0) {
            this.systemConfigDataSource.deleteSystemConfig(getIdSystemFrom(position));
            refreshListView();
        } else {
            Toast.makeText(ContextApplication.getAppContext(), "Вы не можете удалить первый обьект управления", Toast.LENGTH_LONG).show();
        }
    }

    public void addNewSystem() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Введите название");
        //alert.setMessage("Message");
        final EditText input = new EditText(context);
        input.setText("Объект");

        alert.setView(input);

        input.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(input, 0);
                input.setSelection("Объект".length());
                input.setSelectAllOnFocus(true);
            }
        }, 120);

        alert.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                systemConfigDataSource.createSystemConfig(value);
                refreshListView();
            }
        });

        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void refreshListView() {
        this.cursor.close();
        this.cursor = systemConfigDataSource.getAllData();
        this.cursor.moveToFirst();
        changeCursor(this.cursor);
    }

    private int getIdSystemFrom(int position) {
        this.cursor.moveToPosition(position);
        int id = Integer.parseInt(this.cursor
                .getString(this.cursor
                        .getColumnIndex(SystemConfigSQLiteHelper.COLUMN_ID)));
        return id;
    }


}
