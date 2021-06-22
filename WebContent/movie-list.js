// function to get parameter by name
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
    //keep?
    results[2]=results[2].split("%").join("");

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


function handleStarResult(resultData) {
    // adding this to try and figure out AWS error
    console.log(resultData["errorMessage"]);
    console.log(resultData["errorCause"]);
    console.log(resultData["sqlQuery"]);

    $('#star_table tbody').empty();
    result = resultData;
    let starTableBodyElement = jQuery("#movie_list_body");


    //the default is to display 20 results (assuming they haven't selected the chose number of results option)
    // if (num==0){
    //     num=20;
    // }

    if(offset<0 ){
        offset = 0;
        page = 1;
    }
    else if(offset>resultData.length){
        page = resultData.length/rangeint;
        offset = (page-1)*rangeint;
    }

    // console.log(offset);
    // console.log(offset+rangeint);
    // console.log(resultData.length)
    // Iterate through resultData, no more than 20 entries
    for (let i = offset; i < Math.min(offset+rangeint, resultData.length); i++) {
        console.log("made it in the for loop");

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr id=";
        rowHTML += i.toString();
        rowHTML += '>';
        rowHTML += '<th class="row-data">';
        rowHTML +=

            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_name for the link text
            '</a>' +
            "</th>";
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

        let starArray = resultData[i]["movie_stars"].split(',').sort();
        // console.log(resultData[i]["movie_stars"].split(','));
        // console.log(resultData[i]["movie_stars"].split(',')[0].replace(/\s/g, '-').replace('.',''));
        rowHTML += "<th>";
        for (j = 0; j < starArray.length; j++) {
            if (j === 3) {
                break;
            }
            rowHTML += '<a href="single-star.html?name=' + starArray[j].replace(/\s/g, '-').replace('.','') + '">'
                + starArray[j] + '</a>' + ", ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += '<td><input type="button" value="Add" onclick="onClick()" /></td>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);


    }
}



// adding to handle add to cart option
function handleAddItemResult(resultData) {
    let starTableBodyElement = jQuery("#movie_list_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr id=";
        rowHTML += i.toString();
        rowHTML += '>';
        rowHTML += '<th class="row-data">';
        rowHTML +=

            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_name for the link text
            '</a>' +
            "</th>";
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

        let starArray = resultData[i]["movie_stars"].split(',');
        rowHTML += "<th>";
        for (j = 0; j < starArray.length; j++) {
            if (j === 3) {
                break;
            }
            rowHTML += '<a href="single-star.html?name=' + starArray[j].replace(/\s/g, '-').replace('.','') + '">'
                + starArray[j] + '</a>' + ", ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += '<td><input type="button" value="Add" onclick="onClick()" /></td>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

function handlesort(a,b){
    if(a.indexOf("rating") != -1){
        if(b.indexOf("ascend") != -1){
            sortByRating = "asc";
            sortByTitle = null;
        }
        else{
            sortByRating = "desc";
            sortByTitle = null;
        }
    }
    else if(a.indexOf("title") != -1){
        if(b.indexOf("ascend") != -1){
            sortByTitle = "asc";
            sortByRating = null;
        }
        else{
            sortByTitle = "desc";
            sortByRating = null;
        }
    }

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movieList?title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&genre=" + movieGenre +"&sortByTitle="+sortByTitle+"&sortByRating="+sortByRating, // Setting request url, which is mapped by MovieServlet in Stars.java
        success: (resultData) => handleStarResult(resultData)
    });
}

function handleNumItems(a){
    if (a=='100'){
        rangeint=100;
    }
    else if(a=='10'){
        rangeint = 10;
    }
    else if(a=='25'){
        rangeint =25;
    }
    else if(a=='50'){
        rangeint =50;
    }
    handleStarResult(result);
}



// on click function for add to cart
function onClick() {
    let rowId = event.target.parentNode.parentNode.id;
    let data = document.getElementById(rowId).querySelectorAll(".row-data")[0].innerHTML;
    let movTitle = data.split('>')[1];
    movTitle = movTitle.substring(0, movTitle.length - 3);
    console.log("Test rowId is: " + rowId);
    console.log("Test - row movie title: " + movTitle);

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/addItem?title=" + movTitle, // Setting request url, which is mapped by AddItemServlet in AddItemServlet.java
        success: (resultData) => handleAddItemResult(resultData) // Setting callback function to handle data returned successfully by the MovieServlet
    });
}

function handleprevnext(a){
    if(a==true){
        // console.log("clicked prev");
        $(".next").show();
        page = page-1;
        offset = (page-1)*rangeint;
    }
    else{
        // console.log("clicked next");
        $(".prev").show();
        page = page+1;
        offset = (page-1)*rangeint;
    }
    if (page==3 && rangeint==100){
        $(".next").hide();
    }

    // console.log("made it here");
    // console.log("page number"+page);
    handleStarResult(result);
}


// here we need to add functions to get the parameters from the url
let movieTitle = getParameterByName('title');
let movieYear = getParameterByName('year');
let movieDirector = getParameterByName('director');
let movieStar = getParameterByName('star');
let movieGenre = getParameterByName('genre');

let sortByTitle = "";
let sortByRating = "";
// let num = 0;

let pagenumber  = getParameterByName('pagenumber');
let range = getParameterByName('range');
let rangeint=20;
let page = 0;
let offset = 0;

if(!range){
    let rangeint = 20;
}
else{
    let rangeint = parseInt(range);
}

if(!pagenumber){
    // console.log("here1");
    offset = 0;
    page = 1;
}
else{
    // console.log("here2");
    page = parseInt(pagenumber);
    offset = (page-1)*rangeint;
}

console.log("page: "+page);
console.log("page number:"+pagenumber);

if (page==1){
    $(".prev").hide();
}

// console.log(pagenumber);
// console.log(range);
// console.log(rangeint);
//
// console.log(offset);
// console.log(page);



// console.log("Test title: " + movieTitle);
// console.log("Test year: " + movieYear);
// console.log("Test director: " + movieDirector);
// console.log("Test star: " + movieStar);
// console.log("Test genre: "+ movieGenre);

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movieList?title=" + movieTitle + "&year=" + movieYear + "&director=" + movieDirector + "&star=" + movieStar + "&genre=" + movieGenre +"&sortByTitle="+sortByTitle+"&sortByRating="+sortByRating, // Setting request url, which is mapped by MovieServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the MovieServlet
});