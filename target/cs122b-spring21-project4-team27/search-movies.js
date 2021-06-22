let search_form = $("#search_form");

// dictionary to cache autocomplete results in local memory
var autocompleteDict = new Object();

function handleSearchResult(resultData) {
    console.log(resultData);
    // let resultDataJson = JSON.parse(resultDataString);

    console.log("handle search response");

    // If login succeeds, it will redirect the user to index.html
    if (resultData) {
        window.location.replace("movie-list.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    console.log(formSubmitEvent);
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    let title = document.getElementById("title").value;
    let year = document.getElementById("year").value;
    let director = document.getElementById("director").value;
    let star = document.getElementById("star").value;

    window.location.replace("movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star);

    // $.ajax(
    //     "api/movieList", {
    //         method: "GET",
    //         // Serialize the login form to the data sent by GET request
    //         data: search_form.serialize(),
    //         success: handleSearchResult
    //     }
    // );
}

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here
    if (query in autocompleteDict) {
        handleLookupAjaxSuccess(autocompleteDict[query], query, doneCallback);
        console.log("result already cached, retrieving from cache");
        return;
    }


    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/movieSuggestion?query=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here
    if (!(query in autocompleteDict)) {
        autocompleteDict[query] = data;
    }


    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"])

    window.location.replace("single-movie.html?id=" + suggestion["data"]["movieId"]);
}

function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    window.location.replace("movie-list.html?title=" + query + "&year=&director=&star=");
    console.log(query);
}



search_form.submit(submitSearchForm);

// binding autocomplete text fields to its function
$('#title').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters

});

$('#title').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#title').val())
    }
})