package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.BillItemSaveResponse;
import com.ecowaste.smartbilling.model.Bill;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoiceEmailService {

    private static final DateTimeFormatter INVOICE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.otp.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.otp.email.from:}")
    private String fromAddress;

    @Value("${spring.application.name:EcoWaste Smart Billing System}")
    private String applicationName;

    public InvoiceEmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public boolean canSendInvoiceEmail() {
        return emailEnabled && fromAddress != null && !fromAddress.isBlank() && mailSenderProvider.getIfAvailable() != null;
    }

    public void sendInvoice(Bill bill, List<BillItemSaveResponse> items) {
        if (!canSendInvoiceEmail()) {
            throw new IllegalStateException("Invoice email is not configured.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(bill.getCustomerEmail());
        message.setSubject(applicationName + " invoice " + bill.getInvoiceNumber());
        message.setText(buildInvoiceMailBody(bill, items));

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("Mail sender is not available.");
        }

        mailSender.send(message);
    }

    private String buildInvoiceMailBody(Bill bill, List<BillItemSaveResponse> items) {
        String itemLines = items.stream()
                .map(item -> "- " + item.getProductName()
                        + " | Qty " + item.getQuantity()
                        + " | " + item.getLineTotal())
                .reduce("", (left, right) -> left + right + System.lineSeparator());

        return """
                Thank you for shopping with MegaMart.

                Invoice Number: %s
                Customer: %s
                Phone: %s
                Email: %s
                Issued On: %s

                Items:
                %s
                Subtotal: %s
                Applied Returning Discount: %s%% (Rs.%s)
                Final Payable: %s
                Next Visit Reward Earned: %s%%

                Regards,
                MegaMart Smart Billing
                """.formatted(
                bill.getInvoiceNumber(),
                bill.getCustomerName(),
                bill.getCustomerPhone(),
                bill.getCustomerEmail(),
                bill.getCreatedDate().format(INVOICE_TIME_FORMAT),
                itemLines.isBlank() ? "- No items" : itemLines,
                bill.getSubtotal(),
                bill.getAppliedDiscountPercent() == null ? "0.00" : bill.getAppliedDiscountPercent(),
                bill.getAppliedDiscountAmount() == null ? "0.00" : bill.getAppliedDiscountAmount(),
                bill.getTotalAmount(),
                bill.getNextVisitDiscountPercent()
        );
    }
}
