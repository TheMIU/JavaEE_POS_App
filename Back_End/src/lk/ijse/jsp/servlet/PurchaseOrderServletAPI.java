package lk.ijse.jsp.servlet;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = {"/pages/purchase-order"})
public class PurchaseOrderServletAPI extends HttpServlet {

    /*@Override
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

                    String cartItemCode = item.getString("code");
                    int cartQty = item.getInt("qty");

                    PreparedStatement pstm2 = connection.prepareStatement("insert into order_items (orderID, itemID, qty)\n" +
                            "values (?,?,?);");
                    pstm2.setObject(1, orderID);
                    pstm2.setObject(2, cartItemCode);
                    pstm2.setObject(3, String.valueOf(cartQty));

                    pstm2.executeUpdate();

                    int newItemCount = qty - cartQty;
                    System.out.println(newItemCount);

                    // Update item counts in the item table
                    PreparedStatement pstm3 = connection.prepareStatement("update item set qty = ? where code = ?");
                    pstm3.setInt(1, newItemCount);
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
    }*/
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader("Content-Type", "application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");

        try {
            JsonReader reader = Json.createReader(req.getReader());
            JsonObject jsonObject = reader.readObject();

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

            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/testdb?useSSL=false", "root", "1234");
                connection.setAutoCommit(false);

                boolean success = true;

                // Insert order
                PreparedStatement pstm1 = connection.prepareStatement("INSERT INTO orders (orderID, date, customerID, discount, total) VALUES (?, ?, ?, ?, ?)");
                pstm1.setString(1, orderID);
                pstm1.setString(2, date);
                pstm1.setString(3, customerID);
                pstm1.setString(4, discount);
                pstm1.setString(5, total);

                if (pstm1.executeUpdate() <= 0) {
                    success = false;
                }

                // Insert items from the cart
                PreparedStatement pstm2 = connection.prepareStatement("INSERT INTO order_items (orderID, itemID, qty) VALUES (?, ?, ?)");
                PreparedStatement pstm3 = connection.prepareStatement("UPDATE item SET qty = qty - ? WHERE code = ?");

                for (JsonValue cartItemValue : cart) {
                    JsonObject cartItem = (JsonObject) cartItemValue;
                    JsonObject item = cartItem.getJsonObject("item");
                    String cartItemCode = item.getString("code");
                    int cartQty = cartItem.getInt("qty");
                    //System.out.println(cart);
                    //System.out.println(cartQty);

                    pstm2.setString(1, orderID);
                    pstm2.setString(2, cartItemCode);
                    pstm2.setInt(3, cartQty);

                    if (pstm2.executeUpdate() <= 0) {
                        success = false;
                    }

                    pstm3.setInt(1, cartQty);
                    pstm3.setString(2, cartItemCode);

                    if (pstm3.executeUpdate() <= 0) {
                        success = false;
                    }
                }

                if (success) {
                    connection.commit();
                    showMessage(resp, orderID + " Order Successfully Added..!", "ok", "[]");
                    resp.setStatus(200);
                } else {
                    connection.rollback();
                    showMessage(resp, "Data insertion failed", "error", "[]");
                    resp.setStatus(500);
                }

                connection.setAutoCommit(true);

            } catch (SQLException e) {
                e.printStackTrace();
                resp.setStatus(500);
                showMessage(resp, "Database error", "error", "[]");
            }
        } catch (JsonException e) {
            e.printStackTrace();
            resp.setStatus(400);
            showMessage(resp, "Invalid JSON data", "error", "[]");
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
