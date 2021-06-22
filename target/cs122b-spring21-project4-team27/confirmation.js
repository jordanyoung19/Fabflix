// js file for shopping cart



function handleCartResult(resultData) {
    let cartTableBodyElement = jQuery('#order_list_body');

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
        console.log("key: " + movie);
        console.log("value: " + quantity);
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

function handlePaymentSuccess(resultData) {
    console.log(resultData);
    console.log(resultData["orderNumber"]);

    $("#orderNumber").text(resultData["orderNumber"]);
}



jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "POST", // Setting request method
    url: "api/addSale", // Setting request url, which is mapped by AddItemServlet in AddItemServlet.java
    success: (resultData) => handlePaymentSuccess(resultData) // Setting callback function to handle data returned successfully by the MovieServlet
});

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/addItem",
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the MovieServlet
});