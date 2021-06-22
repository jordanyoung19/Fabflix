// js file for shopping cart



function handleCartResult(resultData) {
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

    let i = 0;

    console.log("this is running");

    for (const [movie, quantity] of Object.entries(dict)) {
        console.log("key: " + movie);
        console.log("value: " + quantity);
        movieCount = movieCount + quantity
        let price = quantity * 10;
        let rowHTML = "";
        rowHTML += "<tr id=";
        rowHTML += i.toString();
        rowHTML += '>';
        rowHTML += '<th class="row-data">';
        rowHTML += movie + "</th>";
        rowHTML += "<th>" + quantity + "</th>";
        rowHTML += "<th>" + '$' + price + "</th>";
        rowHTML += '<td><input type="button" value="Remove" onclick="onClick()" /></td>';
        rowHTML += "</tr>";

        i++;
        cartTableBodyElement.append(rowHTML);
    }

    let moviePriceTotal = movieCount * 10;

    $("#totalPrice").text("Total Cost: $" + moviePriceTotal + ".00");

}

function onClick() {
    let rowId = event.target.parentNode.parentNode.id;
    let data = document.getElementById(rowId).querySelectorAll(".row-data")[0].innerHTML;
    console.log("test: " + data);

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/editItems?title=" + data // Setting request url, which is mapped by AddItemServlet in AddItemServlet.java
        // success: (resultData) => handleAddItemResult(resultData) // Setting callback function to handle data returned successfully by the MovieServlet
    });
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/addItem",
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the MovieServlet
});