import JSZip from "jszip";
import fs from "fs";
fs.readFileSync('foo.txt','utf8');

export function load() {
    const data =fs.readFileSync(/Users/jan/Downloads/Basic_LinkedInDataExport_01-19-2021.zip');
    const zip = JSZip();
    zip.load(data);
    // fetch('file:///Users/jan/Downloads/Basic_LinkedInDataExport_01-19-2021.zip')       // 1) fetch the url
    //     .then(function (response) {                       // 2) filter on 200 OK
    //         if (response.status === 200 || response.status === 0) {
    //             return Promise.resolve(response.blob());
    //         } else {
    //             return Promise.reject(new Error(response.statusText));
    //         }
    //     })
    //     .then(JSZip.loadAsync)                            // 3) chain with the zip promise
    //     .then(function (zip) {
    //         return zip.file("Projects.csv").async("string"); // 4) chain with the text content promise
    //     })
    //     .then(function success(text) {
    //         console.log(text);
    //     });
}

load();