var exec = require('cordova/exec');

exports.printerStatus = function(success, error) {
    exec(success, error, "Wepoy", "printerStatus", []);
};

exports.printLine = function(text, fontName, fontSize, fontStyle, success, error) {
    exec(success, error, "Wepoy", "printLine", [text, fontName, fontSize, fontStyle]);
};

exports.setGrayLevel = function(level, success, error) {
    console.log("setGrayLevel: " + level);
    exec(success, error, "Wepoy", "setGrayLevel", [level]);
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
