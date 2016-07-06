var exec = require('cordova/exec');

exports.print = function(text, success, error) {
    exec(success, error, "Wepoy", "print", [text]);
};

exports.scanMagneticStripe = function(success, error) {
    exec(success, error, "Wepoy", "scanMagneticStripe", []);
};

exports.scanBarcode = function(success, error) {
    exec(success, error, "Wepoy", "scanBarcode", []);
};

exports.enableScanner = function(success, error) {
    exec(success, error, "Wepoy", "enableScanner", []);
};
