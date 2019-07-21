package hu.dpal.phonegap.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactNumberPicker extends CordovaPlugin {

    private Context context;
    private CallbackContext callbackContext;
    private static final int CONTACT_PICKER_RESULT = 0;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
    	
        this.callbackContext = callbackContext;
        this.context = cordova.getActivity().getApplicationContext();
        
        if (action.equals("pick")) {
        	
            Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            contactsIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

            cordova.startActivityForResult(this, contactsIntent, CONTACT_PICKER_RESULT);

            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

         if (resultCode == Activity.RESULT_OK) {
            
            JSONObject contact = new JSONObject();

            Uri contactData = data.getData();
            Cursor c =  getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {

                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contact.put("name", name);

                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                contact.put("id", contactId);

                String phoneNumber=""
                String emailAddress="";
 
                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
 
                if ( hasPhone.equalsIgnoreCase("1"))
                    hasPhone = "true";
                else
                    hasPhone = "false" ;
 
                if (Boolean.parseBoolean(hasPhone))
                {
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                    while (phones.moveToNext())
                    {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();

                    contact.put("phoneNumber", phoneNumber);
                }
 
                // Find Email Addresses
                Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,null, null);
                while (emails.moveToNext())
                {
                    emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                contact.put("email", emailAddress);
                emails.close();
 
              }
            c.close();

            callbackContext.success(contact);
        } else if (resultCode == Activity.RESULT_CANCELED) {
        	callbackContext.error("No result");
        	return;
        } else {
        	callbackContext.error("Failed");
        	return;
        }
        
        
   }

}