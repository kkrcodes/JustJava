package com.example.android.justjava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;


/**
 * This app displays an order form to order coffee (extra toppings available!).
 */
public class MainActivity extends AppCompatActivity {

    int quantity = 2;
    boolean hasWhippedCream;
    boolean hasChocolate;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        EditText nameView = findViewById(R.id.text_name);
        name = nameView.getText().toString();
        outState.putString("name", name);
        outState.putInt("quantity", quantity);
        CheckBox whippedCreamCheckBox = findViewById(R.id.checkbox_whipped_cream);
        hasWhippedCream = whippedCreamCheckBox.isChecked();
        outState.putBoolean("hasWhippedCream", hasWhippedCream);
        CheckBox chocolateCheckBox = findViewById(R.id.checkbox_chocolate);
        hasChocolate = chocolateCheckBox.isChecked();
        outState.putBoolean("hasChocolate", hasChocolate);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        name = savedInstanceState.getString("name");
        quantity = savedInstanceState.getInt("quantity");
        hasWhippedCream = savedInstanceState.getBoolean("hasWhippedCream");
        hasChocolate = savedInstanceState.getBoolean("hasChocolate");
        EditText nameView = findViewById(R.id.text_name);
        nameView.setText(name);
        displayQuantity(quantity);
        CheckBox whippedCreamCheckBox = findViewById(R.id.checkbox_whipped_cream);
        whippedCreamCheckBox.setChecked(hasWhippedCream);
        CheckBox chocolateCheckBox = findViewById(R.id.checkbox_chocolate);
        chocolateCheckBox.setChecked(hasChocolate);
    }

    /**
     * Calculates the price of the order.
     *
     * @param addWhippedCream whether or not to add whipped cream to the coffee
     * @param addChocolate    whether or not to add chocolate to the coffee
     * @return total price of the order
     */
    public int calculatePrice(boolean addWhippedCream, boolean addChocolate) {
        //Calculate the price of one cup of coffee
        int basePrice = 10;

        // If the user wants whipped cream, add $1 per cup
        if (addWhippedCream) {
            basePrice += 1;
        }

        // If the user wants chocolate, add $2 per cup
        if (addChocolate) {
            basePrice += 2;
        }
        // Calculate the total order price by multiplying by the quantity
        return basePrice * quantity;
    }

    /**
     * Displays the given quantity value on the screen.
     *
     * @param numberOfCoffees quantity of coffee
     */
    private void displayQuantity(int numberOfCoffees) {
        TextView quantityTextView = findViewById(R.id.text_quantity_value);
        quantityTextView.setText(String.valueOf(numberOfCoffees));
    }

    /**
     * Creates the summary of the order.
     *
     * @param name            name on the order
     * @param price           price of the order
     * @param addWhippedCream whether or not to add whipped cream to the coffee
     * @param addChocolate    whether or not to add chocolate to the coffee
     * @return text summary
     */
    private String createOrderSummary(String name, int price, boolean addWhippedCream,
                                      boolean addChocolate) {
        String orderMessage = getString(R.string.order_summary_name, name);
        orderMessage += "\n" + getString(R.string.order_summary_whipped_cream, addWhippedCream);
        orderMessage += "\n" + getString(R.string.order_summary_chocolate, addChocolate);
        orderMessage += "\n" + getString(R.string.order_summary_quantity, quantity);
        orderMessage += "\n" + getString(R.string.order_summary_price,
                NumberFormat.getCurrencyInstance().format(price));
        orderMessage += "\n" + getString(R.string.thank_you);
        return orderMessage;
    }


    /**
     * Handles clicks from all buttons in the app
     *
     * @param view view from which the click event happened
     */
    public void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.button_minus:
                if (quantity == 1) {
                    Toast.makeText(this, getString(R.string.minimum_coffee_warning), Toast.LENGTH_SHORT).show();
                    break;
                }
                quantity--;
                displayQuantity(quantity);
                break;
            case R.id.button_plus:
                if (quantity == 100) {
                    Toast.makeText(this, getString(R.string.maximum_coffee_warning), Toast.LENGTH_SHORT).show();
                    break;
                }
                quantity++;
                displayQuantity(quantity);
                break;
            case R.id.button_order:
                // Get user's name
                EditText nameView = findViewById(R.id.text_name);
                name = nameView.getText().toString();
                // Check if the user wants whipped cream topping
                CheckBox whippedCreamCheckBox = findViewById(R.id.checkbox_whipped_cream);
                hasWhippedCream = whippedCreamCheckBox.isChecked();
                // Check if the user wants chocolate topping
                CheckBox chocolateCheckBox = findViewById(R.id.checkbox_chocolate);
                hasChocolate = chocolateCheckBox.isChecked();
                // Calculate the order price
                int totalPrice = calculatePrice(hasWhippedCream, hasChocolate);
                //Create the Order Summary to be displayed
                String orderMessage = createOrderSummary(name, totalPrice, hasWhippedCream, hasChocolate);
                // Send an intent to launch an email app with the order summary in the email body
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.order_summary_email_subject, name));
                intent.putExtra(Intent.EXTRA_TEXT, orderMessage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
        }
    }
}