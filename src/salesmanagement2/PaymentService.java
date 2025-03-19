/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanagement2;

/**
 *
 * @author ADMIN
 */
public class PaymentService {

    public boolean processPayment(double amount) {
        // Simulate M-Pesa payment processing
        System.out.println("Processing payment of $" + amount + " via M-Pesa...");

        // Simulate success or failure
        boolean paymentSuccessful = Math.random() < 0.8; // 80% success rate

        if (paymentSuccessful) {
            System.out.println("Payment successful!");
        } else {
            System.out.println("Payment failed. Please try again.");
        }

        return paymentSuccessful;
    }
}

