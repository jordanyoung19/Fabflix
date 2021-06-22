let login_form = $("#login_form");
// let employee_login_button = $("#employeeLogin");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("index.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

function handleEmployeeLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log("getting here");

    console.log(resultDataJson);

    if (resultDataJson["status"] === "success") {
        window.location.replace("_dashboard.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    let rec_response = $(".g-recaptcha-response").val();
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    if (rec_response.length==0){
        alert("You must complete recaptcha before logging in!");
        return;
    }


    if (document.getElementById('employeeLogin').checked === true) {
        console.log("success");
    } else {
        console.log("fail");
    }

    if (!document.getElementById('employeeLogin').checked === true) {
        console.log("user login");
        $.ajax(
            "api/login", {
                method: "POST",
                // Serialize the login form to the data sent by POST request
                data: login_form.serialize(),
                success: handleLoginResult
            }
        );
    } else {
        console.log("employee login");
        $.ajax(
            "api/employeeLogin", {
                method: "POST",
                // Serialize the login form to the data sent by POST request
                data: login_form.serialize(),
                success: (resultData) => handleEmployeeLoginResult(resultData)
            }
        );
        // console.log("got here");
    }
}

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);