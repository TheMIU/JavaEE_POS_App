package lk.ijse.jsp.servlet;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/pages/purchase-order"})
public class PurchaseOrderServletAPI extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader("Content-Type", "application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        // Extract values from the JSON object
        String orderID = jsonObject.getString("orderID");
        String date = jsonObject.getString("date");
        JsonObject customer = jsonObject.getJsonObject("customer");
        JsonArray cart = jsonObject.getJsonArray("cart");
        String total = jsonObject.getString("total");
        String discount = jsonObject.getString("discount");
        if (discount.equals("NaN")) {
            discount = "0";
        }

        String customerID = customer.getString("id");

        System.out.println(cart);

        String itemCode = "";
        int qty = 0;
        for (JsonValue cartItemValue : cart) {
            JsonObject cartItem = (JsonObject) cartItemValue;
            JsonObject item = cartItem.getJsonObject("item");

            itemCode = item.getString("code");
            qty = item.getInt("qty");

            System.out.println(itemCode);
            System.out.println(qty);
        }

        // transaction
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/testdb?useSSL=false", "root", "1234");
            connection.setAutoCommit(false);

            PreparedStatement pstm1 = connection.prepareStatement("insert into orders (orderID, date, customerID, discount, total)\n" +
                    "values (?,?,?,?,?);");
            pstm1.setObject(1, orderID);
            pstm1.setObject(2, date);
            pstm1.setObject(3, customerID);
            pstm1.setObject(4, discount);
            pstm1.setObject(5, total);

            if (pstm1.executeUpdate() > 0) {
                // Insert items from the cart
                for (JsonValue cartItemValue : cart) {
                    JsonObject cartItem = (JsonObject) cartItemValue;
                    JsonObject item = cartItem.getJsonObject("item");

                    itemCode = item.getString("code");
                    qty = item.getInt("qty");

                    PreparedStatement pstm2 = connection.prepareStatement("insert into order_items (orderID, itemID, qty)\n" +
                            "values (?,?,?);");
                    pstm2.setObject(1, orderID);
                    pstm2.setObject(2, itemCode);
                    pstm2.setObject(3, String.valueOf(qty));

                    pstm2.executeUpdate();

                    // Update item counts in the item table
                    PreparedStatement pstm3 = connection.prepareStatement("update item set qty = qty - ? where code = ?");
                    pstm3.setInt(1, qty);
                    pstm3.setString(2, itemCode);
                    pstm3.executeUpdate();
                }

                connection.commit();
                showMessage(resp, orderID + " Order Successfully Added..!", "ok", "[]");
                resp.setStatus(200);
            } else {
                connection.rollback();
                showMessage(resp, "Wrong data", "error", "[]");
                resp.setStatus(400);
            }

            connection.setAutoCommit(true);

        } catch (ClassNotFoundException e) {
            showMessage(resp, e.getMessage(), "error", "[]");
            resp.setStatus(500);

        } catch (SQLException e) {
            showMessage(resp, e.getMessage(), "error", "[]");
            resp.setStatus(400);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "Content-type");
    }

    private void showMessage(HttpServletResponse resp, String message, String state, String data) throws IOException {
        JsonObjectBuilder response = Json.createObjectBuilder();
        response.add("state", state);
        response.add("message", message);
        response.add("data", data);
        resp.getWriter().print(response.build());
    }
}
