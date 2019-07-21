var exec = require('cordova/exec');

module.exports = {

	pick : function(success, fail) {
        exec(success, fail, 'ContactPicker', 'pick', []);
    }
};