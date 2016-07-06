var exec = require('cordova/exec');

exports.print = function(text, success, error) {
    exec(success, error, "Wepoy", "print", [text]);
};

exports.scanBarcode = function(success, error) {
    exec(success, error, "Wepoy", "scanBarcode", []);
};

exports.scanMagneticStripe = function(success, error) {
    exec(success, error, "Wepoy", "scanMagneticStripe", []);
};
