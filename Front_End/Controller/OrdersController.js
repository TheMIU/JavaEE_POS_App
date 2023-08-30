let baseUrl = 'http://localhost:8080/app/pages/';

getAllOrders();

function getAllOrders() {
    $("#tblOrders").empty();

    $.ajax({
        url: baseUrl + 'orders',
        dataType: 'json',
        method: 'GET',
        success: function (orders) {
            for (let i in orders) {
                let order = orders[i];
                let orderId = order.orderID;
                let date = order.date;
                let customerId = order.customerID;
                let itemsIDs = order.itemsIDs;
                let discount = order.discount;
                let total = order.total;

                let row = `<tr>
                <td>${orderId}</td>
                <td>${date}</td>
                <td>${customerId}</td>
                <td>${itemsIDs}</td>
                <td>${discount}</td>
                <td>${total}</td>
                </tr>`;

                $("#tblOrders").append(row);
            }
        },
        error: function (error) {
            alert('Error loading !');
        }
    });
}

// search orders
$("#btnSearch").click(function () {
    let isFound = false;
    var searchText = $("#searchText").val().trim().toLowerCase();
    if (searchText !== "") {
        $("#tblOrders tr").hide();
        $("#tblOrders tr").each(function () {
            var orderId = $(this).find("td:eq(0)").text().trim().toLowerCase();
            if (orderId === searchText) {
                $(this).show();
                isFound = true;
            }
        });
    }

    if (!isFound) {
        alert("Not found! check ID again");
        $("#tblOrders tr").show();
        $("#searchText").val("");
    }
});