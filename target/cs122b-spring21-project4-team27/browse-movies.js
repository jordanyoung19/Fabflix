function handleBrowseResult() {
    console.log("handling button click for browse!");

    $(document).on('click', 'button', function () {
        //get the div id of the clicked button
        let $divId = $(this).closest('div').attr('id');
        //get the name of the clicked button
        let $clicked_button = $(this).text();

        let clicked_title = "";
        let clicked_genre = "";

        if ($divId=="genres_list"){
            clicked_genre=$clicked_button;
        }

        if ($divId=="title_list"){
            clicked_title=$clicked_button;
        }

        console.log(clicked_title);
        console.log(clicked_genre)

        window.location.replace("movie-list.html?title=" + clicked_title + "&genre=" + clicked_genre);

    });
}

function handleGenreResult(resultData) {
    console.log("handle genre results");

    let genreTableBody = jQuery("#genre_list_body");

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>";
        rowHTML += '<a href="movie-list.html?title=&genre=' + resultData[i]["genreName"] + '">' +
            resultData[i]["genreName"] + '</a>' + ", ";
        rowHTML += "</th>";
        rowHTML += "</tr>";

        genreTableBody.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genreList",
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the MovieServlet
});

handleBrowseResult();

// jQuery.ajax({
//     type: "GET",
//     dataType: "json",
//     url: "api/movieList",
//     success: resultData => handleBrowseResult(resultData)
// });