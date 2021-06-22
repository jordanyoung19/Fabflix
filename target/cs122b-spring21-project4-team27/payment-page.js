// js file for shopping cart
let payment_form = $("#payment_form");


function showCart(resultData) {
    let cartTableBodyElement = jQuery('#cart_list_body');

    var dict = {};

    for (let i = 0; i < resultData.length; i++) {
        let title = resultData[i];

        if (title in dict) {
            dict[title] = dict[title] + 1;
        } else {
            dict[title] = 1;
        }
    }

    let movieCount = 0

    for (const [movie, quantity] of Object.entries(dict)) {
        movieCount = movieCount + quantity
        let price = quantity * 10;
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + movie + "</th>";
        rowHTML += "<th>" + quantity + "</th>";
        rowHTML += "<th>" + '$' + price + "</th>";
        rowHTML += "</tr>";

        cartTableBodyElement.append(rowHTML);
    }

    let moviePriceTotal = movieCount * 10;

    $("#totalPrice").text("Total Cost: $" + moviePriceTotal + ".00");
}

function handlePayResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handling pay response");
    console.log(resultDataJson);
    console.log("status: " + resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        console.log("showing error message");
        console.log(resultDataJson["message"]);
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}

function submitPaymentForm(formSubmitEvent) {
    console.log("testing payment confirm button");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/pay", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePayResult
        }
    );
}

payment_form.submit(submitPaymentForm);

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/addItem",
    success: (resultData) => showCart(resultData) // Setting callback function to handle data returned successfully by the AddItemServlet
});
