/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    // starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
    //     "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");


    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#single-star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    //console.log(resultData)
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["starName"] + "</th>";
        if (resultData[i]["starDob"] === null) {
            rowHTML += "<th> N/A </th>";
        } else {
            rowHTML += "<th>" + resultData[i]["starDob"] + "</th>";
        }
        // rowHTML += "<th>" + resultData[i]["starDob"] + "</th>";

        ////FIXXXX!
        let starArray = resultData[i]["starMovies"].split(',');
        let idArray = resultData[i]["movieIds"].split(',');
        console.log(starArray);
        //FOR LOOP to hyperlink all the movies the star has acted in (from single star page)
        rowHTML += "<th>";
        for (i = 0; i < starArray.length; i++) {
            rowHTML += '<a href="single-movie.html?id=' + idArray[i] + '">'
                + starArray[i] + '</a>' + ", ";
        }
        // rowHTML += '<a href="single-movie.html?id=' + starArray[1] + '">'
        //     + starArray[1] + '</a>' + ", ";
        // rowHTML += '<a href="single-movie.html?id=' + starArray[2]  + '">'
        //     + starArray[2] + '</a>';
        rowHTML += "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starName = getParameterByName('name');
//console.log('test: ' + starName);

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?name=" + starName, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
