var exec = require('cordova/exec');

exports.printSomething = function(text, success, error) {
    exec(success, error, "Wepoy", "printSomething", [text]);
};
