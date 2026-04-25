package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.BillRequest;
import com.ecowaste.smartbilling.dto.BillSaveRequest;
import com.ecowaste.smartbilling.dto.BillSaveResponse;
import com.ecowaste.smartbilling.dto.BillHistoryResponse;
import com.ecowaste.smartbilling.dto.BillItemSaveResponse;
import com.ecowaste.smartbilling.dto.BillSummaryItemResponse;
import com.ecowaste.smartbilling.dto.BillSummaryResponse;
import com.ecowaste.smartbilling.dto.CategoryBreakdownResponse;
import com.ecowaste.smartbilling.dto.SmsSummaryResponse;
import com.ecowaste.smartbilling.model.Bill;
import com.ecowaste.smartbilling.model.BillItem;
import com.ecowaste.smartbilling.model.Customer;
import com.ecowaste.smartbilling.model.Product;
import com.ecowaste.smartbilling.model.StoreProfile;
import com.ecowaste.smartbilling.model.WasteCategory;
import com.ecowaste.smartbilling.repository.BillRepository;
import com.ecowaste.smartbilling.repository.CustomerRepository;
import com.ecowaste.smartbilling.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private static final DateTimeFormatter INVOICE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final BillRepository billRepository;
    private final StoreProfileService storeProfileService;
    private final InvoiceEmailService invoiceEmailService;

    public BillingService(ProductRepository productRepository,
                          CustomerRepository customerRepository,
                          BillRepository billRepository,
                          StoreProfileService storeProfileService,
                          InvoiceEmailService invoiceEmailService) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.billRepository = billRepository;
        this.storeProfileService = storeProfileService;
        this.invoiceEmailService = invoiceEmailService;
    }

    public BillSummaryResponse generateBillSummary(Long storeId, BillRequest request) {
        List<BillSummaryItemResponse> selectedItems = new ArrayList<>();
        Map<String, CategoryBreakdownResponse> categoryBreakdown = new LinkedHashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal nextVisitDiscountPercent = BigDecimal.ZERO;
        WasteCategory discountCategory = null;

        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findByIdAndStoreId(itemRequest.getProductId(), storeId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found with ID: " + itemRequest.getProductId()));

            validateStockAvailability(product, itemRequest.getQuantity());

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            selectedItems.add(new BillSummaryItemResponse(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getWasteCategory(),
                    itemRequest.getQuantity(),
                    product.getPrice().setScale(2, RoundingMode.HALF_UP),
                    lineTotal,
                    product.getStockQuantity() - itemRequest.getQuantity()
            ));

            totalAmount = totalAmount.add(lineTotal);
            mergeCategoryBreakdown(categoryBreakdown, product.getWasteCategory(), itemRequest.getQuantity(), lineTotal);

            // When multiple categories are present, the next-visit discount follows the highest benefit.
            BigDecimal candidateDiscount = getEarnedDiscountPercent(product.getWasteCategory());
            if (candidateDiscount.compareTo(nextVisitDiscountPercent) > 0) {
                nextVisitDiscountPercent = candidateDiscount;
                discountCategory = product.getWasteCategory();
            }
        }

        categoryBreakdown = sortCategoryBreakdown(categoryBreakdown);

        return new BillSummaryResponse(
                selectedItems,
                totalAmount.setScale(2, RoundingMode.HALF_UP),
                nextVisitDiscountPercent.setScale(2, RoundingMode.HALF_UP),
                discountCategory,
                categoryBreakdown,
                "Reusable-focused shopping earns a stronger next-visit reward. The saved reward is applied automatically during the customer's following checkout."
        );
    }

    private void mergeCategoryBreakdown(Map<String, CategoryBreakdownResponse> categoryBreakdown,
                                        WasteCategory wasteCategory,
                                        Integer quantity,
                                        BigDecimal lineTotal) {
        String key = wasteCategory.name();
        CategoryBreakdownResponse existing = categoryBreakdown.get(key);
        BigDecimal discountPercent = getEarnedDiscountPercent(wasteCategory).setScale(2, RoundingMode.HALF_UP);

        if (existing == null) {
            categoryBreakdown.put(key, new CategoryBreakdownResponse(
                    quantity,
                    lineTotal.setScale(2, RoundingMode.HALF_UP),
                    discountPercent
            ));
            return;
        }

        categoryBreakdown.put(key, new CategoryBreakdownResponse(
                existing.getTotalQuantity() + quantity,
                existing.getTotalAmount().add(lineTotal).setScale(2, RoundingMode.HALF_UP),
                discountPercent
        ));
    }

    private Map<String, CategoryBreakdownResponse> sortCategoryBreakdown(Map<String, CategoryBreakdownResponse> categoryBreakdown) {
        Map<String, CategoryBreakdownResponse> ordered = new LinkedHashMap<>();
        categoryBreakdown.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> ordered.put(entry.getKey(), entry.getValue()));
        return ordered;
    }

    private BigDecimal getEarnedDiscountPercent(WasteCategory category) {
        return switch (category) {
            // Reusable items create the strongest real-life savings by cutting packaging and repeat procurement.
            case REUSABLE -> new BigDecimal("8");
            // Recyclable items still help, but processing/logistics reduce the recoverable value.
            case RECYCLABLE -> new BigDecimal("4");
            // Safe eco-disposal supports compliance and waste handling, so it earns a smaller reward.
            case ECO_DISPOSAL -> new BigDecimal("2");
        };
    }

    @Transactional
    public BillSaveResponse saveBill(Long storeId, BillSaveRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one product must be selected");
        }

        StoreProfile store = storeProfileService.getStoreOrThrow(storeId);
        String normalizedPhoneNumber = request.getCustomer().getPhoneNumber().trim();
        String normalizedEmail = request.getCustomer().getEmail().trim().toLowerCase();

        // Reuse a customer by phone/email so repeat billing for the same shopper stays smooth.
        Customer customer = customerRepository.findByPhoneNumberAndStoreId(normalizedPhoneNumber, storeId)
                .or(() -> customerRepository.findByEmailAndStoreId(normalizedEmail, storeId))
                .or(() -> customerRepository.findByEmail(normalizedEmail)
                        .map(existingCustomer -> validateCustomerOwnership(existingCustomer, storeId, normalizedEmail)))
                .orElseGet(Customer::new);

        customer.setStore(store);
        customer.setFullName(request.getCustomer().getFullName().trim());
        customer.setPhoneNumber(normalizedPhoneNumber);
        customer.setEmail(normalizedEmail);
        if (customer.getOtpVerified() == null) {
            customer.setOtpVerified(false);
        }
        if (customer.getNextDiscountEligible() == null) {
            customer.setNextDiscountEligible(false);
        }
        if (customer.getNextDiscountPercent() == null) {
            customer.setNextDiscountPercent(ZERO);
        }
        Customer savedCustomer;
        try {
            savedCustomer = customerRepository.save(customer);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(
                    "This customer could not be saved because an older database uniqueness rule is still active. Restart the application once and try again."
            );
        }

        List<BillItem> billItems = new ArrayList<>();
        List<BillItemSaveResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal earnedNextVisitDiscountPercent = BigDecimal.ZERO;
        BigDecimal appliedDiscountPercent = Boolean.TRUE.equals(savedCustomer.getNextDiscountEligible())
                ? normalizeMoney(savedCustomer.getNextDiscountPercent())
                : ZERO;

        Bill bill = new Bill();
        bill.setStore(store);
        bill.setCustomer(savedCustomer);
        bill.setCustomerName(savedCustomer.getFullName());
        bill.setCustomerPhone(savedCustomer.getPhoneNumber());
        bill.setCustomerEmail(savedCustomer.getEmail());
        bill.setCreatedDate(java.time.LocalDateTime.now());
        bill.setInvoiceNumber("PENDING");

        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findByIdAndStoreId(itemRequest.getProductId(), storeId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found with ID: " + itemRequest.getProductId()));

            validateStockAvailability(product, itemRequest.getQuantity());

            BigDecimal unitPrice = product.getPrice().setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            BillItem billItem = BillItem.builder()
                    .bill(bill)
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .wasteCategory(product.getWasteCategory())
                    .build();
            billItems.add(billItem);

            itemResponses.add(new BillItemSaveResponse(
                    product.getId(),
                    product.getName(),
                    product.getWasteCategory(),
                    itemRequest.getQuantity(),
                    unitPrice,
                    lineTotal,
                    product.getStockQuantity() - itemRequest.getQuantity()
            ));

            subtotal = subtotal.add(lineTotal);
            BigDecimal discountPercent = getEarnedDiscountPercent(product.getWasteCategory());
            if (discountPercent.compareTo(earnedNextVisitDiscountPercent) > 0) {
                earnedNextVisitDiscountPercent = discountPercent;
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        BigDecimal normalizedSubtotal = normalizeMoney(subtotal);
        BigDecimal appliedDiscountAmount = normalizeMoney(
                normalizedSubtotal.multiply(appliedDiscountPercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
        );
        BigDecimal finalPayable = normalizeMoney(normalizedSubtotal.subtract(appliedDiscountAmount));

        savedCustomer.setNextDiscountEligible(earnedNextVisitDiscountPercent.compareTo(BigDecimal.ZERO) > 0);
        savedCustomer.setNextDiscountPercent(normalizeMoney(earnedNextVisitDiscountPercent));
        savedCustomer = customerRepository.save(savedCustomer);

        bill.setSubtotal(normalizedSubtotal);
        bill.setAppliedDiscountPercent(normalizeMoney(appliedDiscountPercent));
        bill.setAppliedDiscountAmount(appliedDiscountAmount);
        bill.setTotalAmount(finalPayable);
        bill.setNextVisitDiscountPercent(normalizeMoney(earnedNextVisitDiscountPercent));
        bill.setBillItems(billItems);

        Bill savedBill = billRepository.save(bill);
        savedBill.setInvoiceNumber(buildInvoiceNumber(savedBill));
        savedBill = billRepository.save(savedBill);

        // SMS is simulated and returned with the saved bill response for immediate frontend display.
        SmsSummaryResponse smsSummary = buildSmsSummary(
                savedBill.getCustomerName(),
                savedBill.getSubtotal(),
                savedBill.getAppliedDiscountAmount(),
                savedBill.getTotalAmount(),
                savedBill.getNextVisitDiscountPercent(),
                itemResponses
        );

        String responseMessage = buildBillCompletionMessage(savedBill);
        if (invoiceEmailService.canSendInvoiceEmail()) {
            try {
                invoiceEmailService.sendInvoice(savedBill, itemResponses);
                responseMessage = responseMessage + " Invoice sent to customer email.";
            } catch (Exception exception) {
                responseMessage = responseMessage + " Invoice email could not be sent.";
            }
        }

        return new BillSaveResponse(
                true,
                responseMessage,
                savedBill.getId(),
                savedBill.getInvoiceNumber(),
                savedCustomer.getId(),
                savedBill.getCustomerName(),
                savedBill.getCustomerPhone(),
                savedBill.getCustomerEmail(),
                savedBill.getSubtotal(),
                savedBill.getAppliedDiscountPercent(),
                savedBill.getAppliedDiscountAmount(),
                savedBill.getTotalAmount(),
                savedBill.getNextVisitDiscountPercent(),
                savedBill.getCreatedDate(),
                itemResponses,
                smsSummary
        );
    }

    @Transactional(readOnly = true)
    public BillSaveResponse getBillByInvoiceNumber(Long storeId, String invoiceNumber) {
        Bill bill = billRepository.findByInvoiceNumberAndStoreId(invoiceNumber, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceNumber));

        List<BillItemSaveResponse> itemResponses = bill.getBillItems().stream()
                .map(item -> new BillItemSaveResponse(
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getWasteCategory(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal(),
                        item.getProduct().getStockQuantity()
                ))
                .toList();

        SmsSummaryResponse smsSummary = buildSmsSummary(
                bill.getCustomerName(),
                bill.getSubtotal(),
                bill.getAppliedDiscountAmount(),
                bill.getTotalAmount(),
                bill.getNextVisitDiscountPercent(),
                itemResponses
        );

        return new BillSaveResponse(
                true,
                "Invoice fetched successfully",
                bill.getId(),
                bill.getInvoiceNumber(),
                bill.getCustomer().getId(),
                bill.getCustomerName(),
                bill.getCustomerPhone(),
                bill.getCustomerEmail(),
                bill.getSubtotal(),
                bill.getAppliedDiscountPercent(),
                bill.getAppliedDiscountAmount(),
                bill.getTotalAmount(),
                bill.getNextVisitDiscountPercent(),
                bill.getCreatedDate(),
                itemResponses,
                smsSummary
        );
    }

    @Transactional(readOnly = true)
    public List<BillHistoryResponse> getRecentBills(Long storeId) {
        return billRepository.findTop10ByStoreIdOrderByCreatedDateDesc(storeId).stream()
                .map(bill -> new BillHistoryResponse(
                        bill.getId(),
                        bill.getInvoiceNumber(),
                        bill.getCustomerName(),
                        bill.getTotalAmount(),
                        bill.getNextVisitDiscountPercent(),
                        bill.getCreatedDate()
                ))
                .toList();
    }

    private String buildInvoiceNumber(Bill bill) {
        return "MM"
                + bill.getStore().getId()
                + "-"
                + bill.getCreatedDate().format(INVOICE_DATE_FORMAT)
                + "-"
                + String.format("%05d", bill.getId());
    }

    private void validateStockAvailability(Product product, Integer requestedQuantity) {
        int availableStock = product.getStockQuantity() == null ? 0 : product.getStockQuantity();
        if (requestedQuantity > availableStock) {
            throw new IllegalArgumentException(
                    "Insufficient stock for " + product.getName() + ". Available stock: " + availableStock
            );
        }
    }

    public SmsSummaryResponse buildSmsSummary(String customerName,
                                              BigDecimal subtotal,
                                              BigDecimal appliedDiscountAmount,
                                              BigDecimal totalAmount,
                                              BigDecimal nextVisitDiscountPercent,
                                              List<BillItemSaveResponse> items) {
        String recyclableItems = items.stream()
                .filter(item -> item.getWasteCategory() == WasteCategory.RECYCLABLE)
                .map(item -> item.getProductName() + " x" + item.getQuantity())
                .collect(Collectors.joining(", "));

        if (recyclableItems.isBlank()) {
            recyclableItems = "None";
        }

        String messageText = String.format(
                "MegaMart Billing: Dear %s, subtotal Rs.%s, returning reward saved Rs.%s, final payable Rs.%s. Recyclable items: %s. Next visit reward earned: %s%%. Thank you for shopping with MegaMart.",
                customerName,
                subtotal.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                appliedDiscountAmount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                recyclableItems,
                nextVisitDiscountPercent.setScale(2, RoundingMode.HALF_UP).toPlainString()
        );

        return new SmsSummaryResponse(customerName, messageText);
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value == null ? ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String buildBillCompletionMessage(Bill bill) {
        StringBuilder message = new StringBuilder("Bill saved successfully.");

        if (normalizeMoney(bill.getAppliedDiscountAmount()).compareTo(BigDecimal.ZERO) > 0) {
            message.append(" Returning-customer reward of ")
                    .append(normalizeMoney(bill.getAppliedDiscountPercent()).toPlainString())
                    .append("% reduced this bill by Rs.")
                    .append(normalizeMoney(bill.getAppliedDiscountAmount()).toPlainString())
                    .append(".");
        }

        if (normalizeMoney(bill.getNextVisitDiscountPercent()).compareTo(BigDecimal.ZERO) > 0) {
            message.append(" This visit earned ")
                    .append(normalizeMoney(bill.getNextVisitDiscountPercent()).toPlainString())
                    .append("% off for the next checkout.");
        }

        return message.toString();
    }

    private Customer validateCustomerOwnership(Customer existingCustomer, Long storeId, String normalizedEmail) {
        Long existingStoreId = existingCustomer.getStore() != null
                ? existingCustomer.getStore().getId()
                : null;

        if (existingStoreId != null && !existingStoreId.equals(storeId)) {
            Customer clonedCustomer = new Customer();
            clonedCustomer.setFullName(existingCustomer.getFullName());
            clonedCustomer.setPhoneNumber(existingCustomer.getPhoneNumber());
            clonedCustomer.setEmail(normalizedEmail);
            clonedCustomer.setOtpCode(existingCustomer.getOtpCode());
            clonedCustomer.setOtpVerified(existingCustomer.getOtpVerified());
            clonedCustomer.setNextDiscountEligible(existingCustomer.getNextDiscountEligible());
            clonedCustomer.setNextDiscountPercent(existingCustomer.getNextDiscountPercent());
            return clonedCustomer;
        }

        return existingCustomer;
    }
}
