package be.mkfin.messandcantine.service;


import be.mkfin.messandcantine.entity.Availability;
import be.mkfin.messandcantine.entity.Payement;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {
	
	@Autowired
	private APIContext apiContext;
	
	
	public Payment createPayment(
			Double total, 
			String currency, 
			String method,
			String intent,
			String description, 
			String cancelUrl, 
			String successUrl) throws PayPalRESTException{
		Amount amount = new Amount();
		amount.setCurrency(currency);
		total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
		amount.setTotal(String.format("%.2f", total).replace(",","."));

		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);

		Payer payer = new Payer();
		payer.setPaymentMethod(method);

		Payment payment = new Payment();
		payment.setIntent(intent);
		payment.setPayer(payer);  
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);

		return payment.create(apiContext);
	}
	
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);

	}

	public Refund refund(Payement payement){
		Availability reservation = payement.getCommande().getAvailability();
		Refund refund = new Refund();
		Amount amount = new Amount();
		amount.setTotal((""+reservation.getPrice()).replace(",","."));
		amount.setCurrency("EUR");
		refund.setAmount(amount);
		Sale sale = new Sale();
		sale.setId(payement.getRefundID());
		try {
			// Refund sale
			return  sale.refund(apiContext, refund);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
			System.err.println(e.getDetails());
		}
		return null ;
	}

	public PayoutBatch payTeacher(Payement payement) throws PayPalRESTException {
		BigDecimal ammount = new BigDecimal(payement.getAmount()).setScale(2, RoundingMode.HALF_UP);
		String amountAsString = String.format("%.2f", ammount.doubleValue()).replace(",", ".");
		PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();
		senderBatchHeader.setSenderBatchId(
				""+payement.getId()).setEmailSubject(
				"You have a Payout!");
		Currency amount = new Currency();
		amount.setValue(amountAsString).setCurrency("EUR");
		PayoutItem senderItem = new PayoutItem();
		senderItem.setRecipientType("Email")
				.setNote("Thanks for your patronage")
				.setReceiver("messandcantine@gmail.com")
				.setSenderItemId(""+payement.getId()).setAmount(amount);
		List<PayoutItem> items = new ArrayList<>();
		items.add(senderItem);
		Payout payout = new Payout();
		payout.setSenderBatchHeader(senderBatchHeader).setItems(items);
		PayoutBatch payoutBatch = payout.create(apiContext, null);
		String batchStatus = payoutBatch.getBatchHeader().getBatchStatus();
		System.out.println(batchStatus);
		return payoutBatch ;
	}
}
