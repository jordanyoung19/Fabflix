let star_form = $("#new_star");
let movie_form = $("#new_movie")

function handleAddStarResult(resultDataString) {
    console.log("handleAddStar Function");
    let resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataJson["newId"]);

    $("#add_star_message").text("new star id: " + resultDataJson["newId"]);
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitStarForm(formSubmitEvent) {
    console.log("submit star form");

    formSubmitEvent.preventDefault();
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addStar", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: star_form.serialize(),
            success: handleAddStarResult
        }
    );
}

function handleTableData(resultData) {
    console.log("handling table info");
    console.log(resultData);

    let tablesInfoTable = jQuery("#table_info_body");

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = '';
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["tableName"] + "</th>";
        rowHTML += "<th>" + resultData[i]["attribute"] + "</th>";
        rowHTML += "<th>" + resultData[i]["type"] + "</th>";
        rowHTML += "</tr>";

        tablesInfoTable.append(rowHTML);
    }
}

function handleAddMovieResult(resultDataString) {
    console.log("handle add movie function");
    let resultDataJson = JSON.parse(resultDataString);

    $("#add_movie_message").text(resultDataJson["status"]);
}

function handleMovieForm(formSubmitEvent) {
    console.log("add new movie form");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addMovie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: movie_form.serialize(),
            success: handleAddMovieResult
        }
    );
}

// Bind the submit action of the form to a handler function
star_form.submit(submitStarForm);
movie_form.submit(handleMovieForm);

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/getTableInfo",
    success: (resultData) => handleTableData(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
