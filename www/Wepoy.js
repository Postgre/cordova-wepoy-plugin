var exec = require('cordova/exec');

exports.printerStatus = function(success, error) {
    exec(success, error, "Wepoy", "printerStatus", []);
};

exports.printLine = function(text, success, error) {
    exec(success, error, "Wepoy", "printLine", [text]);
};

exports.printCode = function(text, codeType, success, error) {
    exec(success, error, "Wepoy", "printCode", [text, codeType]);
};

exports.paperFeed = function(amount, success, error) {
    exec(success, error, "Wepoy", "paperFeed", [amount]);
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
