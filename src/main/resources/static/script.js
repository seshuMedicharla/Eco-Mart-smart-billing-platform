const state = {
    currentStep: "register",
    storeCount: 0,
    storeId: null,
    storeName: "",
    storeUsername: "",
    storeLoggedIn: false,
    otpVerified: false,
    generatedOtp: "",
    otpExpiresAt: null,
    otpTimerIntervalId: null,
    msg91Config: null,
    msg91ScriptLoaded: false,
    msg91Initialized: false,
    msg91AccessToken: "",
    products: [],
    productImageRefreshKeys: new Map(),
    updatingInventoryProductIds: new Set(),
    recentBills: [],
    productSearchTerm: "",
    selectedItems: new Map(),
    customer: {
        fullName: "",
        phoneNumber: "",
        email: ""
    },
    savedBill: null,
    smsToastTimeoutId: null,
    smsToastHideTimeoutId: null,
    smsToastRemainingMs: 5000,
    smsToastStartedAt: 0
};

const elements = {
    loginForm: document.getElementById("loginForm"),
    storeForm: document.getElementById("storeForm"),
    customerForm: document.getElementById("customerForm"),
    inventoryForm: document.getElementById("inventoryForm"),
    productForm: document.getElementById("productForm"),
    productImageUpdateForm: document.getElementById("productImageUpdateForm"),
    loadPlatformOverviewButton: document.getElementById("loadPlatformOverviewButton"),
    inventoryShortcutButton: document.getElementById("inventoryShortcutButton"),
    closeInventoryButton: document.getElementById("closeInventoryButton"),
    closeInventoryButtonBottom: document.getElementById("closeInventoryButtonBottom"),
    productGrid: document.getElementById("productGrid"),
    cartSummary: document.getElementById("cartSummary"),
    billOutput: document.getElementById("billOutput"),
    dashboardStats: document.getElementById("dashboardStats"),
    resultOverview: document.getElementById("resultOverview"),
    inventoryOverview: document.getElementById("inventoryOverview"),
    inventoryAvailability: document.getElementById("inventoryAvailability"),
    platformOverviewStats: document.getElementById("platformOverviewStats"),
    platformStoreList: document.getElementById("platformStoreList"),
    recentBillsList: document.getElementById("recentBillsList"),
    invoiceSuggestions: document.getElementById("invoiceSuggestions"),
    smsToast: document.getElementById("smsToast"),
    smsToastMessage: document.getElementById("smsToastMessage"),
    smsToastProgress: document.getElementById("smsToastProgress"),
    refreshProducts: document.getElementById("refreshProducts"),
    refreshDashboard: document.getElementById("refreshDashboard"),
    verifyOtpButton: document.getElementById("verifyOtpButton"),
    regenerateOtpButton: document.getElementById("regenerateOtpButton"),
    backToCustomerButton: document.getElementById("backToCustomerButton"),
    backToOtpButton: document.getElementById("backToOtpButton"),
    printInvoiceButton: document.getElementById("printInvoiceButton"),
    invoiceSearch: document.getElementById("invoiceSearch"),
    searchInvoiceButton: document.getElementById("searchInvoiceButton"),
    generateBillButton: document.getElementById("generateBillButton"),
    nextCustomerButton: document.getElementById("nextCustomerButton"),
    openOnboardingButton: document.getElementById("openOnboardingButton"),
    backToStoreLoginButton: document.getElementById("backToStoreLoginButton"),
    adminStatus: document.getElementById("adminStatus"),
    otpStatus: document.getElementById("otpStatus"),
    stepIndicator: document.getElementById("stepIndicator"),
    generatedOtp: document.getElementById("generatedOtp"),
    otpTimer: document.getElementById("otpTimer"),
    loginMessage: document.getElementById("loginMessage"),
    storeMessage: document.getElementById("storeMessage"),
    customerMessage: document.getElementById("customerMessage"),
    saveCustomerOtpButton: document.getElementById("saveCustomerOtpButton"),
    otpMessage: document.getElementById("otpMessage"),
    billMessage: document.getElementById("billMessage"),
    inventoryMessage: document.getElementById("inventoryMessage"),
    productMessage: document.getElementById("productMessage"),
    username: document.getElementById("username"),
    password: document.getElementById("password"),
    platformUsername: document.getElementById("platformUsername"),
    platformPassword: document.getElementById("platformPassword"),
    storeNameInput: document.getElementById("storeName"),
    storeOwner: document.getElementById("storeOwner"),
    storePhone: document.getElementById("storePhone"),
    storeEmail: document.getElementById("storeEmail"),
    storeAddress: document.getElementById("storeAddress"),
    operatorUsername: document.getElementById("operatorUsername"),
    operatorPassword: document.getElementById("operatorPassword"),
    productSearch: document.getElementById("productSearch"),
    inventoryProduct: document.getElementById("inventoryProduct"),
    inventoryStock: document.getElementById("inventoryStock"),
    customerName: document.getElementById("customerName"),
    customerPhone: document.getElementById("customerPhone"),
    customerEmail: document.getElementById("customerEmail"),
    otpCode: document.getElementById("otpCode"),
    productNameInput: document.getElementById("productNameInput"),
    productPriceInput: document.getElementById("productPriceInput"),
    productImageFileInput: document.getElementById("productImageFileInput"),
    productImagePreview: document.getElementById("productImagePreview"),
    productImagePreviewImg: document.getElementById("productImagePreviewImg"),
    productImageUpdateProduct: document.getElementById("productImageUpdateProduct"),
    productUpdateName: document.getElementById("productUpdateName"),
    productUpdatePrice: document.getElementById("productUpdatePrice"),
    productUpdateCategory: document.getElementById("productUpdateCategory"),
    productUpdateStock: document.getElementById("productUpdateStock"),
    productImageUpdateFileInput: document.getElementById("productImageUpdateFileInput"),
    productCategoryInput: document.getElementById("productCategoryInput"),
    productStockInput: document.getElementById("productStockInput"),
    productImageUpdateMessage: document.getElementById("productImageUpdateMessage")
};

const currency = (value) => `Rs. ${Number(value || 0).toFixed(2)}`;

bindEvents();
initializePage();

function bindEvents() {
    elements.loginForm.addEventListener("submit", onStoreLogin);
    elements.storeForm.addEventListener("submit", onStoreRegister);
    elements.customerForm.addEventListener("submit", onCustomerSaveAndGenerateOtp);
    elements.inventoryForm.addEventListener("submit", onInventoryUpdate);
    elements.productForm.addEventListener("submit", onCreateProduct);
    elements.productImageUpdateForm.addEventListener("submit", onUpdateExistingProductImage);
    elements.loadPlatformOverviewButton.addEventListener("click", loadPlatformOverview);
    elements.inventoryShortcutButton.addEventListener("click", openInventoryWorkspace);
    elements.closeInventoryButton.addEventListener("click", closeInventoryWorkspace);
    elements.closeInventoryButtonBottom.addEventListener("click", closeInventoryWorkspace);
    elements.verifyOtpButton.addEventListener("click", onVerifyOtp);
    elements.regenerateOtpButton.addEventListener("click", onRegenerateOtp);
    elements.backToCustomerButton.addEventListener("click", () => setActiveStep("customer"));
    elements.backToOtpButton.addEventListener("click", () => setActiveStep("otp"));
    elements.refreshProducts.addEventListener("click", loadProducts);
    elements.generateBillButton.addEventListener("click", onGenerateBill);
    elements.nextCustomerButton.addEventListener("click", resetForNextCustomer);
    elements.refreshDashboard.addEventListener("click", loadDashboard);
    elements.printInvoiceButton.addEventListener("click", printInvoice);
    elements.searchInvoiceButton.addEventListener("click", searchInvoice);
    elements.invoiceSearch.addEventListener("input", onInvoiceSearchInput);
    elements.invoiceSearch.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            searchInvoice();
        }
    });
    elements.openOnboardingButton.addEventListener("click", () => setActiveStep("register"));
    elements.backToStoreLoginButton.addEventListener("click", () => setActiveStep("login"));
    elements.customerPhone.addEventListener("input", enforcePhoneNumberInput);
    elements.storePhone.addEventListener("input", enforceStorePhoneInput);
    elements.customerName.addEventListener("blur", normalizeCustomerName);
    elements.customerEmail.addEventListener("blur", normalizeCustomerEmail);
    elements.storeOwner.addEventListener("blur", normalizeStoreOwner);
    elements.storeEmail.addEventListener("blur", normalizeStoreEmail);
    elements.productSearch.addEventListener("input", onProductSearch);
    elements.inventoryProduct.addEventListener("change", syncInventoryStockInput);
    elements.productImageUpdateProduct.addEventListener("change", syncProductUpdateForm);
    elements.inventoryAvailability.addEventListener("click", onInventoryAvailabilityClick);
    elements.productImageFileInput.addEventListener("change", onProductImageFileChange);
    elements.productGrid.addEventListener("click", (event) => {
        const decreaseButton = event.target.closest(".qty-decrease");
        if (decreaseButton) {
            event.stopPropagation();
            updateProductQuantity(Number(decreaseButton.dataset.productId), -1);
            return;
        }

        const productCard = event.target.closest(".product-card");
        if (!productCard) {
            return;
        }

        const productId = Number(productCard.dataset.productId);
        const product = state.products.find((item) => item.id === productId);
        if (!product || Number(product.stockQuantity || 0) <= (state.selectedItems.get(productId) || 0)) {
            elements.billMessage.textContent = `No more stock available for ${product?.name || "this product"}.`;
            return;
        }

        updateProductQuantity(productId, 1);
    });
}

async function initializePage() {
    renderStatus();
    renderCartSummary();
    renderResultOverview();
    renderOtpTimer();
    renderInventoryOverview();
    renderInventoryAvailability();
    renderProductImageUpdateOptions();
    await loadMsg91WidgetConfig();
    await loadStoreStatus();
    renderPlatformOverview([], null);
}

async function loadMsg91WidgetConfig() {
    const data = await apiFetch("/api/customers/otp/config", { skipStoreHeader: true });
    if (!data) {
        return;
    }

    state.msg91Config = data;

    if (!data.enabled) {
        elements.customerMessage.textContent = "MSG91 OTP is not configured yet.";
        return;
    }

    try {
        await initializeMsg91Widget();
    } catch (error) {
        elements.customerMessage.textContent = "MSG91 OTP widget could not be loaded. Please refresh and try again.";
    }
}

async function initializeMsg91Widget() {
    if (!state.msg91Config?.enabled) {
        throw new Error("MSG91 config missing");
    }

    if (!state.msg91ScriptLoaded) {
        await loadMsg91Script();
        state.msg91ScriptLoaded = true;
    }

    if (typeof window.initSendOTP !== "function") {
        throw new Error("MSG91 OTP loader unavailable");
    }

    window.initSendOTP({
        widgetId: state.msg91Config.widgetId,
        tokenAuth: state.msg91Config.tokenAuth,
        exposeMethods: true,
        success: () => {},
        failure: () => {}
    });
    state.msg91Initialized = true;
}

function loadMsg91Script() {
    return new Promise((resolve, reject) => {
        const existingScript = document.getElementById("msg91OtpScript");
        if (existingScript) {
            existingScript.addEventListener("load", resolve, { once: true });
            existingScript.addEventListener("error", reject, { once: true });
            if (typeof window.initSendOTP === "function") {
                resolve();
            }
            return;
        }

        const script = document.createElement("script");
        script.id = "msg91OtpScript";
        script.src = "https://verify.msg91.com/otp-provider.js";
        script.async = true;
        script.onload = resolve;
        script.onerror = reject;
        document.head.appendChild(script);
    });
}

function getMsg91PhoneIdentifier() {
    return `${state.msg91Config?.countryCode || "91"}${state.customer.phoneNumber}`;
}

function buildOtpExpiryTimestamp() {
    const validMinutes = Number(state.msg91Config?.otpValidMinutes || 5);
    return new Date(Date.now() + validMinutes * 60 * 1000).toISOString();
}

function maskPhoneNumber(phoneNumber) {
    const digits = String(phoneNumber || "").replace(/\D/g, "");
    if (digits.length < 4) {
        return digits;
    }
    return `${digits.slice(0, 2)}******${digits.slice(-2)}`;
}

function extractMsg91AccessToken(data) {
    if (!data) {
        return "";
    }

    if (typeof data === "string") {
        return data.trim();
    }

    return String(
        data.accessToken
        || data.access_token
        || data["access-token"]
        || data.token
        || data.authToken
        || data.auth_token
        || data.jwtToken
        || data.jwt_token
        || data.jwt
        || data.data?.accessToken
        || data.data?.access_token
        || data.data?.token
        || data.data?.authToken
        || data.data?.auth_token
        || data.data?.jwtToken
        || data.data?.jwt_token
        || ""
    ).trim();
}

async function loadStoreStatus() {
    const data = await apiFetch("/api/store/status", { skipStoreHeader: true });
    if (!data) {
        setActiveStep("register");
        return;
    }

    state.storeCount = Number(data.totalStores || 0);
    if (state.storeCount > 0) {
        elements.storeMessage.textContent = `${state.storeCount} store account(s) already onboarded.`;
        setActiveStep("login");
    } else {
        setActiveStep("register");
    }
}

async function onStoreRegister(event) {
    event.preventDefault();

    const payload = {
        platformUsername: elements.platformUsername.value.trim(),
        platformPassword: elements.platformPassword.value.trim(),
        storeName: normalizeNameValue(elements.storeNameInput.value),
        ownerName: normalizeNameValue(elements.storeOwner.value),
        phoneNumber: normalizePhoneNumber(elements.storePhone.value),
        email: normalizeEmailValue(elements.storeEmail.value),
        address: String(elements.storeAddress.value || "").trim(),
        operatorUsername: String(elements.operatorUsername.value || "").trim().toLowerCase(),
        operatorPassword: String(elements.operatorPassword.value || "").trim()
    };

    elements.storeNameInput.value = payload.storeName;
    elements.storeOwner.value = payload.ownerName;
    elements.storePhone.value = payload.phoneNumber;
    elements.storeEmail.value = payload.email;
    elements.storeAddress.value = payload.address;
    elements.operatorUsername.value = payload.operatorUsername;

    if (Object.values(payload).some((value) => !String(value || "").trim())) {
        elements.storeMessage.textContent = "Complete all onboarding fields before creating a store account.";
        return;
    }

    if (!isValidCustomerName(payload.ownerName)) {
        elements.storeMessage.textContent = "Owner name should contain only letters and spaces.";
        return;
    }

    if (!isValidPhoneNumber(payload.phoneNumber)) {
        elements.storeMessage.textContent = "Store phone number must be exactly 10 digits.";
        return;
    }

    if (!isValidEmail(payload.email)) {
        elements.storeMessage.textContent = "Enter a valid store email address.";
        return;
    }

    if (payload.operatorUsername.length < 4) {
        elements.storeMessage.textContent = "Store login username must be at least 4 characters.";
        return;
    }

    if (payload.operatorPassword.length < 6) {
        elements.storeMessage.textContent = "Store login password must be at least 6 characters.";
        return;
    }

    const data = await apiFetch("/api/store/register", {
        method: "POST",
        body: JSON.stringify(payload),
        skipStoreHeader: true
    });

    if (!data) {
        return;
    }

    state.storeCount = Number(data.totalStores || state.storeCount + 1);
    elements.storeMessage.textContent = `${data.message} Store login: ${data.operatorUsername}`;
    elements.username.value = data.operatorUsername || payload.operatorUsername;
    elements.password.value = data.operatorPassword || payload.operatorPassword;
    setActiveStep("login");
    await loadPlatformOverview();
}

async function onStoreLogin(event) {
    event.preventDefault();

    const username = elements.username.value.trim().toLowerCase();
    const password = elements.password.value.trim();

    if (username.length < 4) {
        elements.loginMessage.textContent = "Store username must be at least 4 characters.";
        return;
    }

    if (password.length < 6) {
        elements.loginMessage.textContent = "Password must be at least 6 characters.";
        return;
    }

    const data = await apiFetch("/api/auth/store-login", {
        method: "POST",
        body: JSON.stringify({ username, password }),
        skipStoreHeader: true
    });

    if (!data) {
        return;
    }

    state.storeLoggedIn = !!data.success;
    state.storeId = data.storeId || null;
    state.storeName = data.storeName || "";
    state.storeUsername = data.username || username;
    elements.loginMessage.textContent = data.message;
    renderStatus();

    if (!state.storeLoggedIn) {
        return;
    }

    resetStoreSessionData();
    await loadProducts();
    await loadDashboard();
    await loadRecentBills();
    elements.billOutput.innerHTML = state.products.length === 0
        ? '<div class="empty-state">No products found for this store. Add products and update stock before starting customer billing.</div>'
        : `<div class="empty-state">Store workspace ready for ${state.storeName}. Completed invoice details will appear here after checkout.</div>`;
    elements.loginMessage.textContent = state.products.length === 0
        ? `${data.message}. This store has no products yet, so add products and opening stock first.`
        : `${data.message}. Current inventory is loaded for ${state.storeName}.`;
    setActiveStep("customer");
}

async function onCustomerSaveAndGenerateOtp(event) {
    event.preventDefault();

    if (!state.storeLoggedIn) {
        elements.customerMessage.textContent = "Store login is required before creating a customer billing session.";
        return;
    }

    state.customer = {
        fullName: normalizeNameValue(elements.customerName.value),
        phoneNumber: normalizePhoneNumber(elements.customerPhone.value),
        email: normalizeEmailValue(elements.customerEmail.value)
    };

    elements.customerName.value = state.customer.fullName;
    elements.customerPhone.value = state.customer.phoneNumber;
    elements.customerEmail.value = state.customer.email;

    if (!state.customer.fullName || !state.customer.phoneNumber || !state.customer.email) {
        elements.customerMessage.textContent = "Please fill in all customer details.";
        return;
    }

    if (!isValidCustomerName(state.customer.fullName)) {
        elements.customerMessage.textContent = "Enter a valid customer name using letters and spaces only.";
        return;
    }

    if (!isValidPhoneNumber(state.customer.phoneNumber)) {
        elements.customerMessage.textContent = "Phone number must be exactly 10 digits.";
        return;
    }

    if (!isValidEmail(state.customer.email)) {
        elements.customerMessage.textContent = "Enter a valid email address.";
        return;
    }

    if (!state.msg91Config?.enabled || !state.msg91Initialized || typeof window.sendOtp !== "function") {
        elements.customerMessage.textContent = "MSG91 mobile OTP is not ready. Refresh the page and try again.";
        return;
    }

    elements.customerMessage.textContent = "Sending OTP to customer mobile number...";
    if (elements.saveCustomerOtpButton) {
        elements.saveCustomerOtpButton.disabled = true;
        elements.saveCustomerOtpButton.textContent = "Sending OTP...";
    }

    const otpSent = await new Promise((resolve) => {
        window.sendOtp(
            getMsg91PhoneIdentifier(),
            () => resolve(true),
            (error) => {
                elements.customerMessage.textContent = error?.message || "Unable to send OTP through MSG91.";
                resolve(false);
            }
        );
    });

    if (elements.saveCustomerOtpButton) {
        elements.saveCustomerOtpButton.disabled = false;
        elements.saveCustomerOtpButton.textContent = "Save Customer and Send OTP";
    }

    if (!otpSent) {
        return;
    }

    state.generatedOtp = "";
    state.msg91AccessToken = "";
    state.otpVerified = false;
    state.otpExpiresAt = buildOtpExpiryTimestamp();
    state.savedBill = null;
    hideSmsToast();
    elements.otpCode.value = "";
    elements.billMessage.textContent = "";
    elements.customerMessage.textContent = "Customer profile saved successfully.";
    elements.otpMessage.textContent = "OTP sent to the customer's mobile number. Enter it below to continue checkout.";
    elements.generatedOtp.textContent = `Sent to ${maskPhoneNumber(state.customer.phoneNumber)}`;

    startOtpCountdown();
    renderStatus();
    setActiveStep("otp");
}

async function onRegenerateOtp() {
    if (!state.storeLoggedIn) {
        elements.otpMessage.textContent = "Store login is required before sending a verification code.";
        return;
    }

    if (!isValidPhoneNumber(state.customer.phoneNumber)) {
        elements.otpMessage.textContent = "Enter a valid 10-digit phone number before resending the code.";
        return;
    }

    if (!state.msg91Initialized || typeof window.retryOtp !== "function") {
        elements.otpMessage.textContent = "MSG91 mobile OTP is not ready. Refresh the page and try again.";
        return;
    }

    const otpResent = await new Promise((resolve) => {
        window.retryOtp(
            "11",
            () => resolve(true),
            (error) => {
                elements.otpMessage.textContent = error?.message || "Unable to resend OTP.";
                resolve(false);
            }
        );
    });

    if (!otpResent) {
        return;
    }

    state.generatedOtp = "";
    state.msg91AccessToken = "";
    state.otpVerified = false;
    state.otpExpiresAt = buildOtpExpiryTimestamp();
    elements.generatedOtp.textContent = `Sent to ${maskPhoneNumber(state.customer.phoneNumber)}`;
    elements.otpMessage.textContent = "A new OTP has been sent to the customer's mobile number.";
    startOtpCountdown();
    renderStatus();
}

async function onVerifyOtp() {
    if (!state.storeLoggedIn) {
        elements.otpMessage.textContent = "Store login is required before verifying the customer code.";
        return;
    }

    if (!isValidPhoneNumber(state.customer.phoneNumber)) {
        elements.otpMessage.textContent = "Enter a valid 10-digit phone number and generate OTP first.";
        return;
    }

    const otp = elements.otpCode.value.trim();
    if (!/^\d{4}$/.test(otp)) {
        elements.otpMessage.textContent = "Verification code must be exactly 4 digits.";
        return;
    }

    if (!state.msg91Initialized || typeof window.verifyOtp !== "function") {
        elements.otpMessage.textContent = "MSG91 mobile OTP is not ready. Refresh the page and try again.";
        return;
    }

    elements.otpMessage.textContent = "Verifying OTP...";

    const verificationResult = await new Promise((resolve) => {
        window.verifyOtp(
            otp,
            (data) => resolve({
                success: true,
                accessToken: extractMsg91AccessToken(data)
            }),
            (error) => {
                elements.otpMessage.textContent = error?.message || "OTP verification failed.";
                resolve({
                    success: false,
                    accessToken: ""
                });
            }
        );
    });

    if (!verificationResult.success) {
        state.otpVerified = false;
        elements.otpMessage.textContent = "OTP verification failed. Please enter the correct OTP and try again.";
        renderStatus();
        return;
    }

    const accessToken = verificationResult.accessToken;

    if (!accessToken) {
        state.otpVerified = true;
        clearOtpCountdown();
        elements.otpMessage.textContent = "OTP verified successfully.";
        renderStatus();
        setActiveStep("products");
        return;
    }

    const data = await apiFetch("/api/customers/otp/verify", {
        method: "POST",
        body: JSON.stringify({
            phoneNumber: state.customer.phoneNumber,
            accessToken
        })
    });

    if (!data) {
        state.msg91AccessToken = accessToken;
        state.otpVerified = true;
        clearOtpCountdown();
        elements.otpMessage.textContent = "OTP verified successfully.";
        renderStatus();
        setActiveStep("products");
        return;
    }

    state.msg91AccessToken = accessToken;
    state.otpVerified = !!data.verified;
    if (state.otpVerified) {
        clearOtpCountdown();
        setActiveStep("products");
    }

    elements.otpMessage.textContent = data.message;
    renderStatus();
}

async function loadProducts() {
    if (!state.storeId) {
        state.products = [];
        renderInventoryOptions();
        renderProductImageUpdateOptions();
        renderInventoryOverview();
        renderProducts();
        renderCartSummary();
        return;
    }

    const data = await apiFetch("/api/products");
    if (!data) {
        return;
    }

    state.products = Array.isArray(data) ? data : [];
    renderInventoryOptions();
    renderProductImageUpdateOptions();
    renderInventoryOverview();
    renderInventoryAvailability();
    renderProducts();
    renderCartSummary();
}

function markProductImageAsRefreshed(productId) {
    if (!productId) {
        return;
    }

    state.productImageRefreshKeys.set(Number(productId), Date.now());
}

function renderInventoryOptions() {
    if (!elements.inventoryProduct) {
        return;
    }

    const inventoryProducts = [...state.products].sort((left, right) => left.name.localeCompare(right.name));

    if (inventoryProducts.length === 0) {
        elements.inventoryProduct.innerHTML = '<option value="">No products available</option>';
        elements.inventoryStock.value = "";
        return;
    }

    elements.inventoryProduct.innerHTML = inventoryProducts.map((product) => `
        <option value="${product.id}">
            ${product.name} - Stock ${product.stockQuantity ?? 0}
        </option>
    `).join("");

    syncInventoryStockInput();
}

function renderProductImageUpdateOptions() {
    if (!elements.productImageUpdateProduct) {
        return;
    }

    if (state.products.length === 0) {
        elements.productImageUpdateProduct.innerHTML = '<option value="">No products available</option>';
        return;
    }

    elements.productImageUpdateProduct.innerHTML = state.products.map((product) => `
        <option value="${product.id}">
            ${product.name}
        </option>
    `).join("");

    syncProductUpdateForm();
}

function syncProductUpdateForm() {
    const productId = Number(elements.productImageUpdateProduct.value);
    const product = state.products.find((item) => item.id === productId);

    if (!product) {
        elements.productUpdateName.value = "";
        elements.productUpdatePrice.value = "";
        elements.productUpdateCategory.value = "RECYCLABLE";
        elements.productUpdateStock.value = "";
        return;
    }

    elements.productUpdateName.value = product.name || "";
    elements.productUpdatePrice.value = Number(product.price || 0);
    elements.productUpdateCategory.value = product.wasteCategory || "RECYCLABLE";
    elements.productUpdateStock.value = Number(product.stockQuantity || 0);
}

function renderInventoryAvailability() {
    if (!elements.inventoryAvailability) {
        return;
    }

    if (!state.storeLoggedIn) {
        elements.inventoryAvailability.innerHTML = '<div class="empty-state">Store inventory availability will appear after store login.</div>';
        return;
    }

    if (state.products.length === 0) {
        elements.inventoryAvailability.innerHTML = '<div class="empty-state">No products available in this store yet. Add products first.</div>';
        return;
    }

    const inventoryProducts = [...state.products].sort((left, right) => left.name.localeCompare(right.name));

    elements.inventoryAvailability.innerHTML = inventoryProducts.map((product) => `
        <article class="inventory-stock-card">
            <img
                class="inventory-stock-image"
                src="${getProductImage(product)}"
                alt="${product.name}"
                onerror="this.src='https://placehold.co/300x220/e4f2e6/204f34?text=MegaMart+Item'">
            <div class="inventory-stock-body">
                <div class="inventory-stock-top">
                    <div>
                        <strong>${product.name}</strong>
                        <span>${formatCategory(product.wasteCategory)} | ${currency(product.price)}</span>
                    </div>
                    <span class="inventory-stock-badge ${getInventoryLevelClass(product.stockQuantity)}">${getInventoryLevelLabel(product.stockQuantity)}</span>
                </div>
                <div class="inventory-stock-count">Available Stock: <strong>${product.stockQuantity ?? 0}</strong></div>
                <div class="inventory-card-actions">
                    <button
                        type="button"
                        class="inventory-step-button secondary inventory-minus-button"
                        data-product-id="${product.id}"
                        ${state.updatingInventoryProductIds.has(product.id) || Number(product.stockQuantity || 0) === 0 ? "disabled" : ""}
                        aria-label="Reduce one stock unit for ${product.name}">-</button>
                    <button
                        type="button"
                        class="inventory-sync-button secondary"
                        data-product-id="${product.id}">Edit Stock</button>
                    <button
                        type="button"
                        class="inventory-step-button inventory-plus-button"
                        data-product-id="${product.id}"
                        ${state.updatingInventoryProductIds.has(product.id) ? "disabled" : ""}
                        aria-label="Add one stock unit for ${product.name}">+</button>
                </div>
            </div>
        </article>
    `).join("");
}

function onInventoryAvailabilityClick(event) {
    const syncButton = event.target.closest(".inventory-sync-button");
    if (syncButton) {
        const productId = Number(syncButton.dataset.productId);
        elements.inventoryProduct.value = String(productId);
        syncInventoryStockInput();
        elements.inventoryMessage.textContent = "Selected product loaded into the stock update form.";
        return;
    }

    const minusButton = event.target.closest(".inventory-minus-button");
    if (minusButton) {
        const productId = Number(minusButton.dataset.productId);
        adjustInventoryBy(productId, -1);
        return;
    }

    const plusButton = event.target.closest(".inventory-plus-button");
    if (!plusButton) {
        return;
    }

    const productId = Number(plusButton.dataset.productId);
    adjustInventoryBy(productId, 1);
}

async function adjustInventoryBy(productId, delta) {
    if (!state.storeLoggedIn) {
        elements.inventoryMessage.textContent = "Store login is required before updating inventory.";
        return;
    }

    const product = state.products.find((item) => item.id === productId);
    if (!product) {
        elements.inventoryMessage.textContent = "Selected product is not available.";
        return;
    }

    if (state.updatingInventoryProductIds.has(productId)) {
        return;
    }

    const nextStockQuantity = Math.max(0, Number(product.stockQuantity || 0) + delta);
    state.updatingInventoryProductIds.add(productId);
    elements.inventoryMessage.textContent = delta > 0
        ? `Increasing stock for ${product.name}...`
        : `Reducing stock for ${product.name}...`;
    renderInventoryAvailability();

    const data = await apiFetch("/api/products/inventory", {
        method: "PUT",
        body: JSON.stringify({
            productId,
            stockQuantity: nextStockQuantity
        })
    });

    if (!data) {
        state.updatingInventoryProductIds.delete(productId);
        renderInventoryAvailability();
        return;
    }

    product.stockQuantity = data.stockQuantity;
    state.updatingInventoryProductIds.delete(productId);
    elements.inventoryProduct.value = String(productId);
    elements.inventoryStock.value = data.stockQuantity;
    elements.inventoryMessage.textContent = `${data.name} stock updated to ${data.stockQuantity}.`;
    renderInventoryOptions();
    renderInventoryOverview();
    renderInventoryAvailability();
    renderProducts();
    renderCartSummary();
}

function renderInventoryOverview() {
    if (!elements.inventoryOverview) {
        return;
    }

    if (!state.storeLoggedIn) {
        elements.inventoryOverview.innerHTML = "";
        return;
    }

    if (state.products.length === 0) {
        elements.inventoryOverview.innerHTML = '<div class="empty-state">Inventory summary will appear after products are added.</div>';
        return;
    }

    const totalProducts = state.products.length;
    const totalUnits = state.products.reduce((sum, product) => sum + Number(product.stockQuantity || 0), 0);
    const lowStockProducts = state.products.filter((product) => Number(product.stockQuantity || 0) > 0 && Number(product.stockQuantity || 0) <= 5).length;
    const outOfStockProducts = state.products.filter((product) => Number(product.stockQuantity || 0) === 0).length;

    elements.inventoryOverview.innerHTML = `
        <div class="dashboard-card">
            <div>
                <strong>${totalProducts}</strong>
                <span>Products in catalog</span>
            </div>
        </div>
        <div class="dashboard-card">
            <div>
                <strong>${totalUnits}</strong>
                <span>Total stock units</span>
            </div>
        </div>
        <div class="dashboard-card">
            <div>
                <strong>${lowStockProducts}</strong>
                <span>Low stock items</span>
            </div>
        </div>
        <div class="dashboard-card">
            <div>
                <strong>${outOfStockProducts}</strong>
                <span>Out of stock items</span>
            </div>
        </div>
    `;
}

function syncInventoryStockInput() {
    const productId = Number(elements.inventoryProduct.value);
    const product = state.products.find((item) => item.id === productId);
    if (!product) {
        elements.inventoryStock.value = "";
        return;
    }
    elements.inventoryStock.value = product.stockQuantity ?? 0;
}

function renderProducts() {
    const filteredProducts = getFilteredProducts();

    if (filteredProducts.length === 0) {
        elements.productGrid.innerHTML = '<div class="empty-state">No products available for this store yet.</div>';
        return;
    }

    elements.productGrid.innerHTML = filteredProducts.map((product) => `
        <article class="product-card ${Number(product.stockQuantity || 0) === 0 ? "out-of-stock" : ""}" data-product-id="${product.id}">
            <img
                class="product-image"
                src="${getProductImage(product)}"
                alt="${product.name}"
                onerror="this.src='https://placehold.co/600x400/e4f2e6/204f34?text=MegaMart+Product'">
            <div class="product-body">
                <div class="product-meta">
                    <span class="category-chip">${formatCategory(product.wasteCategory)}</span>
                    <span class="price">${currency(product.price)}</span>
                </div>
                <h3 class="product-title">${product.name}</h3>
                <div class="product-actions">
                    <button type="button" class="qty-decrease secondary" data-product-id="${product.id}" aria-label="Reduce quantity of ${product.name}">-</button>
                    <span class="tap-note">Click card to add</span>
                </div>
                <div class="qty-display">
                    Quantity Selected: <strong>${state.selectedItems.get(product.id) || 0}</strong>
                </div>
                <div class="stock-note">Available Stock: ${product.stockQuantity ?? 0}</div>
            </div>
        </article>
    `).join("");
}

function getFilteredProducts() {
    const term = state.productSearchTerm.trim().toLowerCase();
    if (!term) {
        return state.products;
    }

    return state.products.filter((product) => {
        const name = String(product.name || "").toLowerCase();
        const category = formatCategory(product.wasteCategory).toLowerCase();
        return name.includes(term) || category.includes(term);
    });
}

function onProductSearch() {
    state.productSearchTerm = elements.productSearch.value.trim();
    renderProducts();
}

function updateProductQuantity(productId, change) {
    const currentQuantity = state.selectedItems.get(productId) || 0;
    const product = state.products.find((item) => item.id === productId);
    const availableStock = Number(product?.stockQuantity || 0);
    const nextQuantity = Math.max(0, Math.min(currentQuantity + change, availableStock));

    if (nextQuantity === 0) {
        state.selectedItems.delete(productId);
    } else {
        state.selectedItems.set(productId, nextQuantity);
    }

    if (change > 0 && nextQuantity === currentQuantity) {
        elements.billMessage.textContent = `Available stock limit reached for ${product?.name || "this product"}.`;
    }

    renderProducts();
    renderCartSummary();
}

function renderCartSummary() {
    const selectedProducts = state.products.filter((product) => state.selectedItems.has(product.id));

    if (selectedProducts.length === 0) {
        elements.cartSummary.innerHTML = '<div class="empty-state">No products selected yet.</div>';
        return;
    }

    const total = selectedProducts.reduce((sum, product) => {
        const quantity = state.selectedItems.get(product.id);
        return sum + Number(product.price) * quantity;
    }, 0);

    const rows = selectedProducts.map((product) => {
        const quantity = state.selectedItems.get(product.id);
        const lineTotal = Number(product.price) * quantity;

        return `
            <div class="cart-item">
                <div>
                    <strong>${product.name}</strong>
                    <span>${formatCategory(product.wasteCategory)} | Qty ${quantity}</span>
                </div>
                <strong>${currency(lineTotal)}</strong>
            </div>
        `;
    }).join("");

    elements.cartSummary.innerHTML = `${rows}
        <div class="cart-item">
            <div>
                <strong>Current Bill Total</strong>
                <span>Before next-visit discount</span>
            </div>
            <strong>${currency(total)}</strong>
        </div>`;
}

async function onGenerateBill() {
    if (!state.storeLoggedIn) {
        elements.billMessage.textContent = "Store login is required before completing billing.";
        return;
    }

    const items = getSelectedItemsPayload();
    if (!items) {
        return;
    }

    if (!state.customer.fullName || !state.customer.phoneNumber || !state.customer.email) {
        elements.billMessage.textContent = "Please enter customer details first.";
        return;
    }

    if (!state.otpVerified) {
        elements.billMessage.textContent = "Please verify the customer code before completing billing.";
        return;
    }

    const data = await apiFetch("/api/bills/save", {
        method: "POST",
        body: JSON.stringify({
            customer: state.customer,
            items
        })
    });

    if (!data) {
        return;
    }

    state.savedBill = data;
    updateStockFromSavedBill(data.items || []);
    hideSmsToast();
    elements.billMessage.textContent = data.message || "Billing completed successfully.";
    elements.billOutput.innerHTML = buildInvoiceMarkup(data);

    renderResultOverview();
    showSmsToast(data.smsSummary?.messageText || "");
    await loadDashboard();
    await loadRecentBills();
    setActiveStep("result");
}

function updateStockFromSavedBill(items) {
    items.forEach((item) => {
        const product = state.products.find((entry) => entry.id === item.productId);
        if (product && item.remainingStock !== undefined && item.remainingStock !== null) {
            product.stockQuantity = item.remainingStock;
        }
    });
    renderInventoryOptions();
    renderInventoryAvailability();
    renderProducts();
}

async function onCreateProduct(event) {
    event.preventDefault();

    if (!state.storeLoggedIn) {
        elements.productMessage.textContent = "Store login is required before adding products.";
        return;
    }

    const uploadedImagePath = await uploadImageFromInput(elements.productImageFileInput, elements.productMessage);

    if (!uploadedImagePath) {
        elements.productMessage.textContent = "Please upload a product image before saving.";
        return;
    }

    const payload = {
        name: normalizeNameValue(elements.productNameInput.value),
        price: Number(elements.productPriceInput.value),
        image: uploadedImagePath,
        wasteCategory: elements.productCategoryInput.value,
        stockQuantity: Number(elements.productStockInput.value)
    };

    elements.productNameInput.value = payload.name;

    if (!payload.name || !payload.image || !payload.wasteCategory) {
        elements.productMessage.textContent = "Add product name, image, and category before saving.";
        return;
    }

    if (!Number.isFinite(payload.price) || payload.price <= 0) {
        elements.productMessage.textContent = "Enter a valid selling price.";
        return;
    }

    if (!Number.isInteger(payload.stockQuantity) || payload.stockQuantity < 0) {
        elements.productMessage.textContent = "Enter a valid opening stock quantity.";
        return;
    }

    const data = await apiFetch("/api/products", {
        method: "POST",
        body: JSON.stringify(payload)
    });

    if (!data) {
        return;
    }

    state.products.push(data);
    state.products.sort((left, right) => left.name.localeCompare(right.name));
    elements.productForm.reset();
    elements.productCategoryInput.value = "RECYCLABLE";
    clearProductImagePreview();
    elements.productMessage.textContent = `${data.name} added successfully for ${state.storeName}.`;
    renderInventoryOptions();
    renderProductImageUpdateOptions();
    renderInventoryOverview();
    renderInventoryAvailability();
    renderProducts();
    renderCartSummary();
}

async function uploadImageFromInput(fileInput, messageElement) {
    const selectedFile = fileInput?.files?.[0];

    if (!selectedFile) {
        return "";
    }

    const formData = new FormData();
    formData.append("image", selectedFile);

    const data = await apiFetch("/api/products/upload-image", {
        method: "POST",
        body: formData
    });

    if (!data) {
        return "";
    }

    if (messageElement) {
        messageElement.textContent = data.message || "Image uploaded successfully.";
    }
    return data.imagePath || "";
}

async function onUpdateExistingProductImage(event) {
    event.preventDefault();

    if (!state.storeLoggedIn) {
        elements.productImageUpdateMessage.textContent = "Store login is required before updating product data.";
        return;
    }

    const productId = Number(elements.productImageUpdateProduct.value);
    const name = normalizeNameValue(elements.productUpdateName.value);
    const price = Number(elements.productUpdatePrice.value);
    const wasteCategory = elements.productUpdateCategory.value;
    const stockQuantity = Number(elements.productUpdateStock.value);
    const currentProduct = state.products.find((item) => item.id === productId);
    const uploadedImagePath = await uploadImageFromInput(elements.productImageUpdateFileInput, elements.productImageUpdateMessage);

    if (!productId) {
        elements.productImageUpdateMessage.textContent = "Select a product to update.";
        return;
    }

    if (!name) {
        elements.productImageUpdateMessage.textContent = "Enter a valid product name.";
        return;
    }

    if (!Number.isFinite(price) || price <= 0) {
        elements.productImageUpdateMessage.textContent = "Enter a valid selling price.";
        return;
    }

    if (!Number.isInteger(stockQuantity) || stockQuantity < 0) {
        elements.productImageUpdateMessage.textContent = "Enter a valid stock quantity.";
        return;
    }

    const hasNonImageChanges = !!currentProduct && (
        currentProduct.name !== name
        || Number(currentProduct.price) !== price
        || currentProduct.wasteCategory !== wasteCategory
        || Number(currentProduct.stockQuantity) !== stockQuantity
    );

    if (!uploadedImagePath && !hasNonImageChanges) {
        elements.productImageUpdateMessage.textContent = "No changes detected for this product.";
        return;
    }

    let data = null;

    if (uploadedImagePath && !hasNonImageChanges) {
        data = await apiFetch(`/api/products/${productId}/image`, {
            method: "PUT",
            body: JSON.stringify({ image: uploadedImagePath })
        });
    } else {
        data = await apiFetch(`/api/products/${productId}`, {
            method: "PUT",
            body: JSON.stringify({
                name,
                price,
                wasteCategory,
                stockQuantity,
                image: uploadedImagePath || null
            })
        });
    }

    if (!data) {
        return;
    }

    const productIndex = state.products.findIndex((item) => item.id === data.id);
    if (productIndex >= 0) {
        state.products[productIndex] = data;
    }

    if (uploadedImagePath) {
        markProductImageAsRefreshed(data.id);
    }

    elements.productImageUpdateForm.reset();
    elements.productImageUpdateMessage.textContent = `${data.name} updated successfully.`;
    state.products.sort((left, right) => left.name.localeCompare(right.name));
    renderProductImageUpdateOptions();
    renderInventoryOverview();
    renderInventoryAvailability();
    renderInventoryOptions();
    renderProducts();
}

function showSmsToast(messageText) {
    const message = String(messageText || "").trim();
    if (!elements.smsToast || !message) {
        return;
    }

    if (state.smsToastTimeoutId) {
        window.clearTimeout(state.smsToastTimeoutId);
        state.smsToastTimeoutId = null;
    }

    if (state.smsToastHideTimeoutId) {
        window.clearTimeout(state.smsToastHideTimeoutId);
        state.smsToastHideTimeoutId = null;
    }

    state.smsToastRemainingMs = 5000;
    state.smsToastStartedAt = Date.now();
    elements.smsToastMessage.textContent = message;
    elements.smsToast.classList.remove("hidden");
    resetSmsToastProgress();

    window.requestAnimationFrame(() => {
        elements.smsToast.classList.add("show");
        elements.smsToastProgress.classList.add("running");
    });

    scheduleSmsToastHide(state.smsToastRemainingMs);
}

function scheduleSmsToastHide(delayMs) {
    if (state.smsToastTimeoutId) {
        window.clearTimeout(state.smsToastTimeoutId);
    }

    state.smsToastRemainingMs = delayMs;
    state.smsToastStartedAt = Date.now();
    state.smsToastTimeoutId = window.setTimeout(() => {
        hideSmsToast();
    }, delayMs);
}

function hideSmsToast() {
    if (!elements.smsToast) {
        return;
    }

    if (state.smsToastTimeoutId) {
        window.clearTimeout(state.smsToastTimeoutId);
        state.smsToastTimeoutId = null;
    }

    if (state.smsToastHideTimeoutId) {
        window.clearTimeout(state.smsToastHideTimeoutId);
        state.smsToastHideTimeoutId = null;
    }

    resetSmsToastProgress();
    elements.smsToast.classList.remove("show");
    state.smsToastHideTimeoutId = window.setTimeout(() => {
        elements.smsToast.classList.add("hidden");
        elements.smsToastMessage.textContent = "";
        state.smsToastHideTimeoutId = null;
    }, 280);
}

function resetSmsToastProgress() {
    if (!elements.smsToastProgress) {
        return;
    }

    elements.smsToastProgress.classList.remove("running");
    elements.smsToastProgress.style.animationDuration = `${state.smsToastRemainingMs}ms`;
    elements.smsToastProgress.style.transform = "scaleX(1)";
    void elements.smsToastProgress.offsetWidth;
}

function renderResultOverview() {
    if (!elements.resultOverview) {
        return;
    }

    if (!state.savedBill) {
        elements.resultOverview.innerHTML = `
            <div class="result-summary-card">
                <span>Current Store</span>
                <strong>${escapeHtml(state.storeName || "MegaMart Store")}</strong>
            </div>
            <div class="result-summary-card">
                <span>Latest Invoice</span>
                <strong>Not generated yet</strong>
            </div>
            <div class="result-summary-card">
                <span>Customer</span>
                <strong>Waiting for billing</strong>
            </div>
            <div class="result-summary-card">
                <span>Bill Total</span>
                <strong>${currency(0)}</strong>
            </div>
        `;
        return;
    }

    elements.resultOverview.innerHTML = `
        <div class="result-summary-card">
            <span>Current Store</span>
            <strong>${escapeHtml(state.savedBill.storeName || state.storeName || "MegaMart Store")}</strong>
        </div>
        <div class="result-summary-card">
            <span>Latest Invoice</span>
            <strong>${escapeHtml(state.savedBill.invoiceNumber || "-")}</strong>
        </div>
        <div class="result-summary-card">
            <span>Customer</span>
            <strong>${escapeHtml(state.savedBill.customerName || "-")}</strong>
        </div>
        <div class="result-summary-card">
            <span>Subtotal</span>
            <strong>${currency(state.savedBill.subtotal)}</strong>
        </div>
        <div class="result-summary-card">
            <span>Discount Applied</span>
            <strong>${currency(state.savedBill.appliedDiscountAmount)}</strong>
        </div>
        <div class="result-summary-card">
            <span>Final Payable</span>
            <strong>${currency(state.savedBill.totalAmount)}</strong>
        </div>
        <div class="result-summary-card">
            <span>Next Visit Reward</span>
            <strong>${Number(state.savedBill.nextVisitDiscountPercent || 0).toFixed(2)}%</strong>
        </div>
    `;
}

function resetStoreSessionData() {
    state.otpVerified = false;
    state.generatedOtp = "";
    state.otpExpiresAt = null;
    state.selectedItems.clear();
    state.productSearchTerm = "";
    state.customer = { fullName: "", phoneNumber: "", email: "" };
    state.savedBill = null;
    hideSmsToast();
    elements.customerForm.reset();
    elements.inventoryForm.reset();
    elements.productForm.reset();
    elements.productImageUpdateForm.reset();
    clearProductImagePreview();
    elements.otpCode.value = "";
    elements.productSearch.value = "";
    elements.invoiceSearch.value = "";
    elements.customerMessage.textContent = "";
    elements.otpMessage.textContent = "";
    elements.billMessage.textContent = "";
    elements.inventoryMessage.textContent = "";
    elements.productMessage.textContent = "";
    elements.productImageUpdateMessage.textContent = "";
    elements.generatedOtp.textContent = "Waiting for OTP";
    elements.billOutput.innerHTML = '<div class="empty-state">Saved bill details will be shown here.</div>';
    clearOtpCountdown();
    renderStatus();
    renderResultOverview();
    renderOtpTimer();
    renderInventoryOverview();
    renderInventoryAvailability();
    renderProductImageUpdateOptions();
}

function resetForNextCustomer() {
    state.otpVerified = false;
    state.generatedOtp = "";
    state.otpExpiresAt = null;
    state.selectedItems.clear();
    state.productSearchTerm = "";
    state.customer = { fullName: "", phoneNumber: "", email: "" };
    state.savedBill = null;
    hideSmsToast();

    elements.customerForm.reset();
    elements.otpCode.value = "";
    elements.productSearch.value = "";
    elements.invoiceSearch.value = "";
    elements.customerMessage.textContent = "";
    elements.otpMessage.textContent = "";
    elements.billMessage.textContent = "";
    elements.generatedOtp.textContent = "Waiting for OTP";
    elements.billOutput.innerHTML = '<div class="empty-state">Saved bill details will be shown here.</div>';

    clearOtpCountdown();
    renderStatus();
    renderProducts();
    renderCartSummary();
    renderResultOverview();
    renderOtpTimer();
    renderInventoryOverview();
    renderInventoryAvailability();
    setActiveStep(state.products.length === 0 ? "result" : "customer");
}

async function loadDashboard() {
    if (!state.storeId) {
        elements.dashboardStats.innerHTML = '<div class="empty-state">Store dashboard will load after store login.</div>';
        return;
    }

    const data = await apiFetch("/api/dashboard/stats");
    if (!data) {
        return;
    }

    const stats = [
        ["Store", state.storeName || "Current Store"],
        ["Total Sales", currency(data.totalSales)],
        ["Recyclable Items Sold", data.recyclableItemsSold ?? 0],
        ["Reusable Items Sold", data.reusableItemsSold ?? 0],
        ["Eco-Disposal Items Sold", data.ecoDisposalItemsSold ?? 0],
        ["Total Bills Generated", data.totalBillsGenerated ?? 0]
    ];

    elements.dashboardStats.innerHTML = stats.map(([label, value]) => `
        <div class="dashboard-card">
            <div>
                <strong>${label}</strong>
                <span>Live value from store database</span>
            </div>
            <strong>${value}</strong>
        </div>
    `).join("");
}

async function loadPlatformOverview() {
    const username = elements.platformUsername.value.trim();
    const password = elements.platformPassword.value.trim();

    if (!username || !password) {
        elements.storeMessage.textContent = "Enter platform admin credentials to load the overview.";
        return;
    }

    const data = await apiFetch("/api/platform/overview", {
        method: "POST",
        body: JSON.stringify({ username, password }),
        skipStoreHeader: true
    });

    if (!data) {
        return;
    }

    renderPlatformOverview(data.stores || [], data);
}

function renderPlatformOverview(stores, overview) {
    if (!overview) {
        elements.platformOverviewStats.innerHTML = '<div class="empty-state">Platform overview will appear here.</div>';
        elements.platformStoreList.innerHTML = '<div class="empty-state">Onboarded stores will appear here.</div>';
        return;
    }

    const stats = [
        ["Total Stores", overview.totalStores ?? 0],
        ["Active Stores", overview.activeStores ?? 0],
        ["Platform Bills", overview.totalPlatformBills ?? 0],
        ["Platform Sales", currency(overview.totalPlatformSales)]
    ];

    elements.platformOverviewStats.innerHTML = stats.map(([label, value]) => `
        <div class="dashboard-card">
            <div>
                <strong>${label}</strong>
                <span>Platform-wide value</span>
            </div>
            <strong>${value}</strong>
        </div>
    `).join("");

    if (!stores.length) {
        elements.platformStoreList.innerHTML = '<div class="empty-state">No stores onboarded yet.</div>';
        return;
    }

    elements.platformStoreList.innerHTML = stores.map((store) => `
        <div class="history-card">
            <div>
                <strong>${store.storeName}</strong>
                <span>${store.ownerName} | Login: ${store.loginUsername} | ${store.active ? "Active" : "Inactive"}</span>
            </div>
            <div>
                <strong>${currency(store.totalSales)}</strong>
                <span>Products ${store.totalProducts} | Customers ${store.totalCustomers} | Bills ${store.totalBills}</span>
            </div>
        </div>
    `).join("");
}

async function loadRecentBills() {
    if (!state.storeId) {
        state.recentBills = [];
        elements.recentBillsList.innerHTML = '<div class="empty-state">Recent invoices will load after store login.</div>';
        renderInvoiceSuggestions();
        return;
    }

    const data = await apiFetch("/api/bills/recent");
    if (!data) {
        return;
    }

    state.recentBills = Array.isArray(data) ? data : [];
    renderInvoiceSuggestions(elements.invoiceSearch.value.trim());

    if (state.recentBills.length === 0) {
        elements.recentBillsList.innerHTML = '<div class="empty-state">No invoices available for this store yet.</div>';
        return;
    }

    elements.recentBillsList.innerHTML = state.recentBills.map((bill) => `
        <button type="button" class="history-card secondary" data-invoice-number="${bill.invoiceNumber}">
            <div>
                <strong>${bill.invoiceNumber}</strong>
                <span>${bill.customerName} | ${bill.createdDate}</span>
            </div>
            <strong>${currency(bill.totalAmount)}</strong>
        </button>
    `).join("");

    elements.recentBillsList.querySelectorAll("[data-invoice-number]").forEach((button) => {
        button.addEventListener("click", () => {
            elements.invoiceSearch.value = button.dataset.invoiceNumber;
            searchInvoice();
        });
    });
}

function onInvoiceSearchInput() {
    renderInvoiceSuggestions(elements.invoiceSearch.value.trim());
}

function renderInvoiceSuggestions(searchTerm = "") {
    if (!elements.invoiceSuggestions) {
        return;
    }

    const term = String(searchTerm || "").trim().toLowerCase();
    if (!term) {
        elements.invoiceSuggestions.innerHTML = "";
        return;
    }

    const matches = state.recentBills.filter((bill) => {
        const invoice = String(bill.invoiceNumber || "").toLowerCase();
        const customer = String(bill.customerName || "").toLowerCase();
        return invoice.includes(term) || customer.includes(term);
    }).slice(0, 5);

    if (!matches.length) {
        elements.invoiceSuggestions.innerHTML = '<div class="empty-state">No matching invoices found.</div>';
        return;
    }

    elements.invoiceSuggestions.innerHTML = matches.map((bill) => `
        <button type="button" class="history-card secondary" data-suggested-invoice="${bill.invoiceNumber}">
            <div>
                <strong>${bill.invoiceNumber}</strong>
                <span>${bill.customerName}</span>
            </div>
            <strong>${currency(bill.totalAmount)}</strong>
        </button>
    `).join("");

    elements.invoiceSuggestions.querySelectorAll("[data-suggested-invoice]").forEach((button) => {
        button.addEventListener("click", async () => {
            elements.invoiceSearch.value = button.dataset.suggestedInvoice;
            elements.invoiceSuggestions.innerHTML = "";
            await searchInvoice();
        });
    });
}

function getSelectedItemsPayload() {
    const items = Array.from(state.selectedItems.entries()).map(([productId, quantity]) => ({
        productId,
        quantity
    }));

    if (items.length === 0) {
        elements.billMessage.textContent = "Select at least one product.";
        return null;
    }

    const hasInvalidQuantity = items.some((item) => !Number.isInteger(item.quantity) || item.quantity < 1);
    if (hasInvalidQuantity) {
        elements.billMessage.textContent = "Each selected product quantity must be at least 1.";
        return null;
    }

    return items;
}

function buildInvoiceMarkup(data) {
    const itemsMarkup = (data.items || []).map((item, index) => `
        <tr>
            <td>${index + 1}</td>
            <td>${item.productName}</td>
            <td>${formatCategory(item.wasteCategory)}</td>
            <td>${item.quantity}</td>
            <td>${currency(item.unitPrice)}</td>
            <td>${currency(item.lineTotal)}</td>
        </tr>
    `).join("");

    return `
        <article class="invoice-document" data-print-invoice="true">
            <header class="invoice-header">
                <div class="invoice-brand">
                    <p class="invoice-kicker">MegaMart Smart Billing</p>
                    <h3>Retail Sales Invoice</h3>
                    <span>Store-ready billing document for customer checkout and retail operations.</span>
                </div>
                <div class="invoice-badge">
                    <strong>Invoice</strong>
                    <span>${data.invoiceNumber || "Not available"}</span>
                </div>
            </header>

            <section class="invoice-grid">
                <div class="invoice-block">
                    <span class="invoice-label">Store</span>
                    <strong>${escapeHtml(state.storeName || "MegaMart Store")}</strong>
                    <span>Bill ID: ${data.billId}</span>
                </div>
                <div class="invoice-block">
                    <span class="invoice-label">Issued To</span>
                    <strong>${escapeHtml(data.customerName || "-")}</strong>
                    <span>${escapeHtml(data.customerPhone || "-")}</span>
                    <span>${escapeHtml(data.customerEmail || "-")}</span>
                </div>
                <div class="invoice-block">
                    <span class="invoice-label">Issued On</span>
                    <strong>${escapeHtml(data.createdDate || "-")}</strong>
                    <span>Next Visit Reward Earned: ${Number(data.nextVisitDiscountPercent || 0).toFixed(2)}%</span>
                </div>
            </section>

            <table class="invoice-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Product</th>
                        <th>Category</th>
                        <th>Qty</th>
                        <th>Unit Price</th>
                        <th>Line Total</th>
                    </tr>
                </thead>
                <tbody>
                    ${itemsMarkup || '<tr><td colspan="6">No invoice items available.</td></tr>'}
                </tbody>
            </table>

            <footer class="invoice-footer">
                <div class="invoice-note">
                    <strong>Store Note</strong>
                    <span>Reusable items earn the strongest next-visit reward because they directly cut packaging and repurchase costs in real retail operations.</span>
                </div>
                <div class="invoice-totals">
                    <span>Subtotal: ${currency(data.subtotal)}</span>
                    <span>Applied Returning Discount (${Number(data.appliedDiscountPercent || 0).toFixed(2)}%): -${currency(data.appliedDiscountAmount)}</span>
                    <strong>Total Payable: ${currency(data.totalAmount)}</strong>
                    <small>Next checkout reward saved: ${Number(data.nextVisitDiscountPercent || 0).toFixed(2)}%</small>
                </div>
            </footer>
        </article>
    `;
}

function renderStatus() {
    elements.adminStatus.textContent = state.storeLoggedIn
        ? `${state.storeName || "Store"} signed in`
        : "Store not signed in";
    elements.otpStatus.textContent = state.otpVerified ? "Verified" : "Verification Pending";
    elements.inventoryShortcutButton.classList.toggle("hidden", !state.storeLoggedIn);
    elements.generateBillButton.disabled = !state.storeLoggedIn;
    elements.verifyOtpButton.disabled = !state.storeLoggedIn;
    elements.regenerateOtpButton.disabled = !state.storeLoggedIn;
}

function openInventoryWorkspace() {
    if (!state.storeLoggedIn) {
        elements.loginMessage.textContent = "Store login is required before opening inventory.";
        return;
    }

    setActiveStep("inventory");
}

function closeInventoryWorkspace() {
    if (!state.storeLoggedIn) {
        setActiveStep("login");
        return;
    }

    if (state.otpVerified) {
        setActiveStep("products");
        return;
    }

    if (state.customer.fullName && state.customer.phoneNumber && state.customer.email) {
        setActiveStep("otp");
        return;
    }

    setActiveStep("customer");
}

function startOtpCountdown() {
    clearOtpCountdown();
    renderOtpTimer();

    if (!state.otpExpiresAt) {
        return;
    }

    state.otpTimerIntervalId = window.setInterval(() => {
        const expired = renderOtpTimer();
        if (expired) {
            clearOtpCountdown();
            state.generatedOtp = "";
            state.otpExpiresAt = null;
            state.otpVerified = false;
            elements.generatedOtp.textContent = "Expired";
            elements.otpMessage.textContent = "Verification code expired. Please resend OTP.";
            renderStatus();
        }
    }, 1000);
}

function clearOtpCountdown() {
    if (state.otpTimerIntervalId) {
        window.clearInterval(state.otpTimerIntervalId);
        state.otpTimerIntervalId = null;
    }
}

function renderOtpTimer() {
    if (!state.otpExpiresAt) {
        elements.otpTimer.textContent = "Waiting for OTP";
        return false;
    }

    const expiry = new Date(state.otpExpiresAt);
    const remainingMs = expiry.getTime() - Date.now();

    if (remainingMs <= 0) {
        elements.otpTimer.textContent = "Expired";
        return true;
    }

    const totalSeconds = Math.floor(remainingMs / 1000);
    const minutes = String(Math.floor(totalSeconds / 60)).padStart(2, "0");
    const seconds = String(totalSeconds % 60).padStart(2, "0");
    elements.otpTimer.textContent = `${minutes}:${seconds} remaining`;
    return false;
}

function setActiveStep(stepName) {
    state.currentStep = stepName;

    document.querySelectorAll(".step-panel").forEach((panel) => {
        panel.classList.toggle("active", panel.id === `step-${stepName}`);
    });

    updateStepIndicator(stepName);
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function updateStepIndicator(activeStep) {
    const stepOrder = ["register", "login", "customer", "otp", "products", "result"];
    const activeIndex = stepOrder.indexOf(activeStep);

    elements.stepIndicator.querySelectorAll("[data-step-indicator]").forEach((item, index) => {
        item.classList.toggle("active", activeIndex >= 0 && index === activeIndex);
        item.classList.toggle("done", index < activeIndex);
    });
}

function formatCategory(category) {
    return String(category || "").replaceAll("_", "-");
}

function getInventoryLevelLabel(stockQuantity) {
    const quantity = Number(stockQuantity || 0);
    if (quantity === 0) {
        return "Out of Stock";
    }
    if (quantity <= 5) {
        return "Low Stock";
    }
    return "In Stock";
}

function getInventoryLevelClass(stockQuantity) {
    const quantity = Number(stockQuantity || 0);
    if (quantity === 0) {
        return "danger";
    }
    if (quantity <= 5) {
        return "warning";
    }
    return "healthy";
}

function getProductImage(product) {
    const image = String(product?.image || "").trim();

    if (!image) {
        return "https://placehold.co/600x400/e4f2e6/204f34?text=MegaMart+Product";
    }

    if (image.startsWith("http://") || image.startsWith("https://") || image.startsWith("/")) {
        const refreshKey = state.productImageRefreshKeys.get(Number(product?.id || 0));
        if (!refreshKey) {
            return image;
        }

        const separator = image.includes("?") ? "&" : "?";
        return `${image}${separator}v=${refreshKey}`;
    }

    return `https://placehold.co/600x400/e4f2e6/204f34?text=${encodeURIComponent(product.name || "MegaMart Product")}`;
}

function isValidPhoneNumber(phoneNumber) {
    return /^\d{10}$/.test(String(phoneNumber || "").trim());
}

function isValidCustomerName(name) {
    return /^[A-Za-z ]{2,100}$/.test(String(name || "").trim());
}

function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(email || "").trim());
}

function normalizePhoneNumber(phoneNumber) {
    return String(phoneNumber || "").replace(/\D/g, "").slice(0, 10);
}

function normalizeNameValue(name) {
    return String(name || "").replace(/\s+/g, " ").trim();
}

function normalizeEmailValue(email) {
    return String(email || "").trim().toLowerCase();
}

function enforcePhoneNumberInput() {
    elements.customerPhone.value = normalizePhoneNumber(elements.customerPhone.value);
}

function normalizeCustomerName() {
    elements.customerName.value = normalizeNameValue(elements.customerName.value);
}

function normalizeCustomerEmail() {
    elements.customerEmail.value = normalizeEmailValue(elements.customerEmail.value);
}

function enforceStorePhoneInput() {
    elements.storePhone.value = normalizePhoneNumber(elements.storePhone.value);
}

function normalizeStoreOwner() {
    elements.storeOwner.value = normalizeNameValue(elements.storeOwner.value);
}

function normalizeStoreEmail() {
    elements.storeEmail.value = normalizeEmailValue(elements.storeEmail.value);
}

function printInvoice() {
    if (!state.savedBill) {
        elements.billMessage.textContent = "Generate a bill before printing the invoice.";
        return;
    }

    const invoiceMarkup = elements.billOutput?.innerHTML?.trim();
    if (!invoiceMarkup) {
        elements.billMessage.textContent = "Invoice content is not ready for printing yet.";
        return;
    }

    const printWindow = window.open("", "_blank", "width=980,height=760");
    if (!printWindow) {
        elements.billMessage.textContent = "Allow pop-ups to print the invoice.";
        return;
    }

    printWindow.document.open();
    printWindow.document.write(`
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${escapeHtml(state.savedBill.invoiceNumber || "MegaMart Invoice")}</title>
            <style>
                * { box-sizing: border-box; }
                body {
                    margin: 0;
                    padding: 24px;
                    background: #ffffff;
                    color: #173c2d;
                    font-family: "Segoe UI", Arial, sans-serif;
                }
                .print-shell {
                    max-width: 980px;
                    margin: 0 auto;
                }
                .invoice-document {
                    display: grid;
                    gap: 20px;
                    color: #173c2d;
                    background: #ffffff;
                    border: 1px solid #d7e6da;
                    border-radius: 18px;
                    padding: 24px;
                }
                .invoice-header,
                .invoice-footer {
                    display: flex;
                    justify-content: space-between;
                    gap: 22px;
                    align-items: stretch;
                }
                .invoice-brand {
                    display: grid;
                    gap: 6px;
                    max-width: 560px;
                }
                .invoice-kicker,
                .invoice-label {
                    margin: 0 0 6px;
                    text-transform: uppercase;
                    letter-spacing: 0.12em;
                    font-size: 11px;
                    font-weight: 700;
                    color: #5d7a6c;
                }
                .invoice-header h3 {
                    margin: 0;
                    font-size: 30px;
                    line-height: 1.1;
                }
                .invoice-header span,
                .invoice-block span,
                .invoice-note span {
                    color: #4f6c5d;
                    font-size: 14px;
                }
                .invoice-badge {
                    min-width: 220px;
                    min-height: 132px;
                    padding: 18px;
                    border-radius: 20px;
                    background: #173c2d;
                    color: #eff8f1;
                    display: grid;
                    align-content: space-between;
                }
                .invoice-badge strong,
                .invoice-badge span {
                    display: block;
                }
                .invoice-badge strong {
                    font-size: 14px;
                }
                .invoice-badge span {
                    font-size: 15px;
                    line-height: 1.45;
                }
                .invoice-grid {
                    display: grid;
                    grid-template-columns: repeat(3, minmax(0, 1fr));
                    gap: 16px;
                }
                .invoice-block {
                    padding: 18px;
                    border-radius: 18px;
                    background: #f7fbf7;
                    border: 1px solid #e1eee4;
                    display: grid;
                    gap: 8px;
                    min-height: 140px;
                }
                .invoice-block strong {
                    display: block;
                    font-size: 16px;
                }
                .invoice-table {
                    width: 100%;
                    border-collapse: collapse;
                    table-layout: fixed;
                    border: 1px solid #e1eee4;
                    border-radius: 16px;
                    overflow: hidden;
                }
                .invoice-table th,
                .invoice-table td {
                    padding: 12px 14px;
                    text-align: left;
                    border-bottom: 1px solid #e1eee4;
                    font-size: 14px;
                }
                .invoice-table th {
                    background: #edf6ef;
                    font-size: 11px;
                    text-transform: uppercase;
                    letter-spacing: 0.08em;
                }
                .invoice-table th:nth-child(1),
                .invoice-table td:nth-child(1) { width: 44px; }
                .invoice-table th:nth-child(4),
                .invoice-table td:nth-child(4) { width: 72px; text-align: center; }
                .invoice-table th:nth-child(5),
                .invoice-table td:nth-child(5),
                .invoice-table th:nth-child(6),
                .invoice-table td:nth-child(6) { width: 110px; text-align: right; }
                .invoice-table tbody tr:last-child td { border-bottom: none; }
                .invoice-note {
                    flex: 1;
                    padding: 6px 0;
                }
                .invoice-note strong,
                .invoice-totals strong {
                    display: block;
                }
                .invoice-totals {
                    min-width: 240px;
                    padding: 20px 22px;
                    border-radius: 18px;
                    background: #edf6ef;
                    border: 1px solid #dcebdd;
                    display: grid;
                    gap: 8px;
                    align-content: center;
                }
                .invoice-totals span {
                    color: #4f6c5d;
                    font-size: 12px;
                    text-transform: uppercase;
                    letter-spacing: 0.08em;
                    font-weight: 700;
                }
                .invoice-totals strong {
                    font-size: 28px;
                    color: #173c2d;
                }
                .invoice-totals small {
                    color: #5d7a6c;
                    font-size: 12px;
                }
                @media print {
                    body { padding: 0; }
                    .print-shell { max-width: none; }
                    .invoice-document {
                        border: none;
                        border-radius: 0;
                        padding: 0;
                    }
                }
            </style>
        </head>
        <body>
            <div class="print-shell">${invoiceMarkup}</div>
        </body>
        </html>
    `);
    printWindow.document.close();
    printWindow.focus();
    printWindow.onload = () => {
        printWindow.print();
        printWindow.close();
    };
}

async function searchInvoice() {
    if (!state.storeLoggedIn) {
        elements.billMessage.textContent = "Store login is required before searching invoices.";
        return;
    }

    const invoiceNumber = elements.invoiceSearch.value.trim();
    if (!invoiceNumber) {
        elements.billMessage.textContent = "Enter an invoice number to search.";
        return;
    }

    elements.billMessage.textContent = `Searching invoice ${invoiceNumber}...`;
    elements.invoiceSuggestions.innerHTML = "";

    const data = await apiFetch(`/api/bills/invoice/${encodeURIComponent(invoiceNumber)}`);
    if (!data) {
        state.savedBill = null;
        hideSmsToast();
        elements.billOutput.innerHTML = `<div class="empty-state">No invoice found for ${escapeHtml(invoiceNumber)} in this store.</div>`;
        renderResultOverview();
        return;
    }

    state.savedBill = data;
    hideSmsToast();
    elements.billOutput.innerHTML = buildInvoiceMarkup(data);
    elements.billMessage.textContent = `Invoice ${invoiceNumber} loaded successfully.`;
    renderResultOverview();
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

async function onInventoryUpdate(event) {
    event.preventDefault();

    if (!state.storeLoggedIn) {
        elements.inventoryMessage.textContent = "Store login is required before updating inventory.";
        return;
    }

    const productId = Number(elements.inventoryProduct.value);
    const stockQuantity = Math.max(0, Number(elements.inventoryStock.value));

    if (!productId) {
        elements.inventoryMessage.textContent = "Select a product to update.";
        return;
    }

    if (!Number.isInteger(stockQuantity) || stockQuantity < 0) {
        elements.inventoryMessage.textContent = "Enter a valid stock quantity.";
        return;
    }

    if (state.updatingInventoryProductIds.has(productId)) {
        return;
    }

    state.updatingInventoryProductIds.add(productId);
    renderInventoryAvailability();

    const data = await apiFetch("/api/products/inventory", {
        method: "PUT",
        body: JSON.stringify({ productId, stockQuantity })
    });

    if (!data) {
        state.updatingInventoryProductIds.delete(productId);
        renderInventoryAvailability();
        return;
    }

    const product = state.products.find((item) => item.id === data.id);
    if (product) {
        product.stockQuantity = data.stockQuantity;
    }

    state.updatingInventoryProductIds.delete(productId);
    elements.inventoryMessage.textContent = `${data.name} stock updated to ${data.stockQuantity}.`;
    renderInventoryOptions();
    renderInventoryOverview();
    renderInventoryAvailability();
    renderProducts();
    renderCartSummary();
}

async function apiFetch(url, options = {}) {
    try {
        const isFormDataBody = options.body instanceof FormData;
        const headers = {
            ...(isFormDataBody ? {} : { "Content-Type": "application/json" }),
            ...(options.headers || {})
        };

        if (!options.skipStoreHeader && state.storeId) {
            headers["X-Store-Id"] = String(state.storeId);
        }

        const response = await fetch(url, {
            ...options,
            headers
        });

        const contentType = response.headers.get("content-type") || "";
        const isJson = contentType.includes("application/json");
        const data = isJson ? await response.json() : null;
        const text = !isJson ? await response.text() : "";

        if (!response.ok) {
            const fallbackMessage = extractErrorMessageFromHtml(text) || "Request failed.";
            handleApiError(url, data?.message || fallbackMessage);
            return null;
        }

        return data;
    } catch (error) {
        handleApiError(url, "Unable to connect to the backend. Please start the Spring Boot server.");
        return null;
    }
}

function handleApiError(url, message) {
    if (url.includes("/api/store")) {
        elements.storeMessage.textContent = message;
        return;
    }

    if (url.includes("/api/auth")) {
        elements.loginMessage.textContent = message;
        return;
    }

    if (url.includes("/api/customers/otp/generate")) {
        elements.customerMessage.textContent = message;
        elements.otpMessage.textContent = message;
        return;
    }

    if (url.includes("/otp")) {
        elements.otpMessage.textContent = message;
        return;
    }

    if (url.includes("/api/products/inventory")) {
        elements.inventoryMessage.textContent = message;
        return;
    }

    if (url.includes("/api/bills")) {
        elements.billMessage.textContent = message;
        return;
    }

    if (url === "/api/products" || url.includes("/api/products")) {
        elements.productMessage.textContent = message;
        elements.billMessage.textContent = message;
        return;
    }

    elements.billMessage.textContent = message;
}

function onProductImageFileChange() {
    const selectedFile = elements.productImageFileInput.files?.[0];

    if (!selectedFile) {
        clearProductImagePreview();
        return;
    }

    if (elements.productImagePreviewImg.src && elements.productImagePreviewImg.src.startsWith("blob:")) {
        URL.revokeObjectURL(elements.productImagePreviewImg.src);
    }

    const previewUrl = URL.createObjectURL(selectedFile);
    elements.productImagePreviewImg.src = previewUrl;
    elements.productImagePreview.classList.remove("hidden");
    elements.productMessage.textContent = `${selectedFile.name} selected. Save the product to upload it.`;
}

function clearProductImagePreview() {
    if (elements.productImagePreviewImg.src && elements.productImagePreviewImg.src.startsWith("blob:")) {
        URL.revokeObjectURL(elements.productImagePreviewImg.src);
    }

    elements.productImagePreviewImg.src = "";
    elements.productImagePreview.classList.add("hidden");
}

function extractErrorMessageFromHtml(htmlText) {
    const text = String(htmlText || "");
    if (!text) {
        return "";
    }

    if (text.includes("Whitelabel Error Page")) {
        return "The request failed on the server. Please check the Spring Boot console for the exact error.";
    }

    return text.replace(/<[^>]*>/g, " ").replace(/\s+/g, " ").trim();
}
