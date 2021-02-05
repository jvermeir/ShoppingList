"use strict";
exports.__esModule = true;
var jszip_1 = require("jszip");
function load() {
    fetch('Basic_LinkedInDataExport_01-19-2021.zip') // 1) fetch the url
        .then(function (response) {
        if (response.status === 200 || response.status === 0) {
            return Promise.resolve(response.blob());
        }
        else {
            return Promise.reject(new Error(response.statusText));
        }
    })
        .then(jszip_1["default"].loadAsync) // 3) chain with the zip promise
        .then(function (zip) {
        return zip.file("Projects.csv").async("string"); // 4) chain with the text content promise
    })
        .then(function success(text) {
        console.log(text);
    });
}
exports.load = load;
load();
