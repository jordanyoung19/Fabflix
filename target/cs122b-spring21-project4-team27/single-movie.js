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

    // think we can delete lines 41-49 but not totally sure
    console.log("handleResult: populating star info from resultData");
    // populate the star info h3
    // find the empty h3 body by id "star_info"
    // let movieInfoElement = jQuery("#movie_info");
    //
    // // append two html <p> created to the h3 body, which will refresh the page
    // movieInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
    //     "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#single_movie_table");

    console.log(resultData);

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr id=";
        rowHTML += i.toString();
        rowHTML += '>';
        rowHTML += '<th class="row-data">';
        rowHTML += resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        // rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
        let genreArray = resultData[i]["movie_genres"].split(',').sort();
        rowHTML += "<th>";
        for (p = 0; p < genreArray.length; p++) {
            if (p == 3) {
                break;
            }
            rowHTML += '<a href="movie-list.html?title=&genre=' + genreArray[p] + '">'
                + genreArray[p] + '</a>' + ", ";
        }
        rowHTML += "</th>";

        let starArr = resultData[i]["movie_stars"].split(',').sort();

        rowHTML += "<th>";
        // rowHTML += '<a href="single-star.html?name=' + starArr[0].replace(/\s/g, '-').replace('.','') + '">'
        //     + starArr[0] + '</a>' + ", ";
        // rowHTML += '<a href="single-star.html?name=' + starArr[1].replace(/\s/g, '-').replace('.','') + '">'
        //     + starArr[1] + '</a>' + ", ";
        // rowHTML += '<a href="single-star.html?name=' + starArr[2].replace(/\s/g, '-').replace('.','') + '">'
        //     + starArr[2] + '</a>';
        for (j = 0; j < starArr.length; j++) {
            if (j === 3) {
                break;
            }
            rowHTML += '<a href="single-star.html?name=' + starArr[j].replace(/\s/g, '-').replace('.','') + '">'
                + starArr[j] + '</a>' + ", ";
        }
        rowHTML += "</th>";
        // rowHTML += "<th>" + resultData[i]["movie_stars"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += '<td><input type="button" value="Add" onclick="onClick()" /></td>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

// onClick function for add to cart button
function onClick() {
    let rowId = event.target.parentNode.parentNode.id;
    console.log("rowId: " + rowId);
    console.log("test: " + document.getElementById(rowId).querySelectorAll(".row-data")[0].innerHTML);
    let data = document.getElementById(rowId).querySelectorAll(".row-data")[0].innerHTML;
    // let movTitle = data.split('>')[1];
    // movTitle = movTitle.substring(0, movTitle.length - 3);
    console.log("Test rowId is: " + rowId);
    console.log("Test - row movie title: " + data);

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/addItem?title=" + data // Setting request url, which is mapped by AddItemServlet in AddItemServlet.java
        // success: (resultData) => handleAddItemResult(resultData) // Setting callback function to handle data returned successfully by the MovieServlet
    });
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by sServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});