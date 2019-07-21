# cordova-plugin-contactpicker
Cordova plugin to choose a contact from the native address book

Usage:

    // Show Contact Picker
    var successCallback = function(result){
        setTimeout(function(){alert(result.name + " " + result.phoneNumber);},0);
    };
    var failedCallback = function(result){
        setTimeout(function(){alert(result);},0);
    }
    window.plugins.contactPicker.pick(successCallback,failedCallback);


