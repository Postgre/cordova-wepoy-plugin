var exec = require('cordova/exec');

exports.printSomething = function(text, success, error) {
    exec(success, error, "Wepoy", "printSomething", [text]);
};

exports.scanSomething = function(success, error) {
    exec(success, error, "Wepoy", "scanSomething", []);
};

exports.listenToScan = function(success, error) {
    exec(success, error, "Wepoy", "listenToScan", []);
};


exports.scanMagCard = function(success, error) {
    exec(success, error, "Wepoy", "scanMagCard", []);
};
