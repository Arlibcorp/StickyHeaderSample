package com.networks.testapplication.ui.adapters_viewholders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.networks.testapplication.R;
import com.networks.testapplication.data.UpcomingGuest;
import com.networks.testapplication.utils.AppPreferencesHelper;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MyViewHolder extends BaseViewHolder {
    public TextView name, email, arrivalDate, shortNoticeButton, cancelButton;


    RecyclerDataCallback<UpcomingGuest> mDatacallback;
    UpcomingGuestListAdapter.Callback mCallback;

    public MyViewHolder(@NonNull View itemView,
                        RecyclerDataCallback<UpcomingGuest> mDatacallback,
                        UpcomingGuestListAdapter.Callback mCallback) {
        super(itemView);
        Timber.d("viewholder butterknife bind");
        name = itemView.findViewById(R.id.upcoming_guest_name);
        email = itemView.findViewById(R.id.upcoming_guest_email);
        arrivalDate = itemView.findViewById(R.id.arrival_date);
        shortNoticeButton = itemView.findViewById(R.id.short_notice_registration);
        cancelButton = itemView.findViewById(R.id.upcoming_guests_cancel_button);

        ButterKnife.bind(this, itemView);

        this.mDatacallback = mDatacallback;
        this.mCallback = mCallback;
    }

    @Override
    public void clear() {

    }

    public void onBind(int position) {
        super.onBind(position);
        final UpcomingGuest guest = mDatacallback.getItemDataInPosition(position);

        Timber.d("guest names are:%s%s",guest.getFirstName(),guest.getEmail());
        String guestWholeName = guest.getFirstName() + " "+ guest.getLastName();
        if( guest.getFirstName() != null && guest.getLastName() != null) {
            name.setText(guestWholeName);
        }



        if (guest.getEmail() != null) {
            email.setText(guest.getEmail());
            email.setVisibility(View.VISIBLE);
        } else {
            email.setText("");
            email.setVisibility(View.GONE);

        }

        if(guest.isShortNoticeRegistration() != null && guest.isShortNoticeRegistration().equals("true")){
            shortNoticeButton.setVisibility(View.VISIBLE);
        } else {
            shortNoticeButton.setVisibility(View.GONE);
        }

        if( guest.getMeetingDateStart() != null && guest.getMeetingDateEnd() != null) {
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
            DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
            ZoneId location = ZoneId.of(AppPreferencesHelper.getInstance().getDefaultTimeZone());
            ZoneId deviceLocation = ZoneId.of(TimeZone.getDefault().getID());
            TimeZone tz = TimeZone.getTimeZone(AppPreferencesHelper.getInstance().getDefaultTimeZone());
            ZonedDateTime startTime = OffsetDateTime.parse(guest.getMeetingDateStart())
                    .atZoneSameInstant(location);
            ZonedDateTime endTime = OffsetDateTime.parse(guest.getMeetingDateEnd())
                    .atZoneSameInstant(location);
            String startDate = startTime.format(outputFormatter);
            String endDate = endTime.format(endFormatter);
            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.US);
            try {
                date = format.parse(guest.getMeetingDateStart());
                System.out.println(date);
            } catch (ParseException e) {
                Timber.e(e, "getEventTime() exception log");
            }
            if( !location.equals(deviceLocation)) {
                Timber.d("final date is zzz: " + location + deviceLocation);
                String locationName = tz.getDisplayName(tz.inDaylightTime(date), TimeZone.SHORT);

                arrivalDate.setText(startDate + " - " + endDate + " " + locationName);

            } else {
                arrivalDate.setText(startDate + " - " + endDate);

            }

            shortNoticeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(), R.style.AppTheme);
                    builder.setTitle("SHORT NOTICE REGISTRATION");
                    if (AppPreferencesHelper.getInstance().getVisitorRegistrationLeadTime() != 0) {
                        builder.setMessage("Your guest will be registered in the building's system after " + AppPreferencesHelper.getInstance().getVisitorRegistrationLeadTime() + " minutes from now. If your guest is expected to arrive earlier, please contact your Experience Manager for assistance.");
                    } else {
                        builder.setMessage("Your guest will be registered in the building's system within 5 minutes from now. If your guest is expected to arrive earlier, please contact your Experience Manager for assistance.");

                    }
                    builder.setCancelable(true);
                    builder.setPositiveButton(R.string.ok,null);
                    builder.create().show();

                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener positiveListener = (dialog, which) -> {
                        mCallback.onItemClick( guest.getFirstName(), guest.getLastName(), guest.getEmail());
                    };
                    DialogInterface.OnClickListener negativeListener = (dialog, which) -> dialog.dismiss();

                    mCallback.showDialog(R.string.alert_confirm_delete, R.string.alert_are_you_sure, R.string.yes, positiveListener, R.string.no, negativeListener, null);


                }
            });
        }

    }

}
