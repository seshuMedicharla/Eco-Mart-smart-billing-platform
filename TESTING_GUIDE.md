# EcoWaste Smart Billing System Testing Guide

## Scope

This guide covers:

- Backend API testing
- Frontend functional testing
- Login testing
- OTP testing
- Product selection testing
- Bill generation testing
- Dashboard testing
- Edge case validation

## Test Environment

- Backend: Spring Boot
- Frontend: HTML, CSS, JavaScript
- Database: MySQL
- API Testing Tool: Postman
- Browser Testing: Chrome / Edge / Firefox

## Backend API Test Cases

| Test Case ID | Module | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|
| API-001 | Admin Login | Username=`admin`, Password=`admin123` | Login successful JSON response with `success=true` | __________ | __________ |
| API-002 | Admin Login | Username=`admin`, Password=`wrong123` | Unauthorized or failed JSON response with invalid credentials message | __________ | __________ |
| API-003 | Admin Login | Empty username and password | Validation error message | __________ | __________ |
| API-004 | OTP Generate | Phone=`9876543210` | 4-digit OTP generated and returned in JSON | __________ | __________ |
| API-005 | OTP Generate | Phone=`98765` | Validation error: phone number must be exactly 10 digits | __________ | __________ |
| API-006 | OTP Verify | Correct phone and correct OTP | JSON response with `verified=true` | __________ | __________ |
| API-007 | OTP Verify | Correct phone and wrong OTP | JSON response with `verified=false` and invalid OTP message | __________ | __________ |
| API-008 | OTP Verify | OTP not generated, only phone sent | JSON response with no OTP found message | __________ | __________ |
| API-009 | Product Fetch | `GET /api/products` | JSON array of all available products | __________ | __________ |
| API-010 | Product Fetch | Database contains seeded products | All products returned with `name`, `price`, `image`, `wasteCategory` | __________ | __________ |
| API-011 | Bill Save | Valid customer + valid selected items | Bill saved successfully with bill ID, totals, items, SMS summary | __________ | __________ |
| API-012 | Bill Save | Valid customer + empty item list | Validation error: at least one product must be selected | __________ | __________ |
| API-013 | Bill Save | Quantity `0` for one product | Validation error: quantity must be at least 1 | __________ | __________ |
| API-014 | Bill Save | Invalid product ID | JSON error response: product not found | __________ | __________ |
| API-015 | Bill Save | Customer phone not 10 digits | Validation error for phone number | __________ | __________ |
| API-016 | Bill Save | Customer name contains digits/symbols | Validation error for customer name | __________ | __________ |
| API-017 | Dashboard | `GET /api/dashboard/stats` after at least one bill saved | Total sales, category counts, and bill count returned correctly | __________ | __________ |
| API-018 | Dashboard | `GET /api/dashboard/stats` with no bills in database | Zero values returned for totals and item counts | __________ | __________ |
| API-019 | SMS Summary | Save bill with recyclable items | SMS summary included in save bill response | __________ | __________ |
| API-020 | SMS Summary | Save bill with no recyclable items | SMS summary still generated with recyclable items shown as `None` | __________ | __________ |

## Frontend Functional Test Cases

| Test Case ID | Module | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|
| UI-001 | Page Load | Open application in browser | All sections load correctly without UI breakage | __________ | __________ |
| UI-002 | Admin Login | Click login with valid credentials | Status changes to Logged in and success message appears | __________ | __________ |
| UI-003 | Admin Login | Click login with invalid credentials | Error message displayed in login section | __________ | __________ |
| UI-004 | Customer Details | Enter valid name, phone, email and submit | Customer message shown and demo OTP displayed | __________ | __________ |
| UI-005 | Customer Details | Leave one field empty | Frontend validation message shown | __________ | __________ |
| UI-006 | OTP Verification | Enter correct OTP and click verify | OTP status changes to Verified | __________ | __________ |
| UI-007 | OTP Verification | Enter wrong OTP and click verify | Invalid OTP message shown | __________ | __________ |
| UI-008 | OTP Verification | Click regenerate OTP | New demo OTP displayed | __________ | __________ |
| UI-009 | Product Grid | Load page | Products appear in a neat responsive grid with image, category, price, quantity field | __________ | __________ |
| UI-010 | Product Selection | Increase quantity for one product | Cart summary updates immediately | __________ | __________ |
| UI-011 | Product Selection | Set quantity back to `0` | Product removed from cart summary | __________ | __________ |
| UI-012 | Bill Generation | Click Generate Bill with valid customer, OTP, and items | Final bill details shown in output panel | __________ | __________ |
| UI-013 | Bill Generation | Click Generate Bill without OTP verification | Validation message shown and bill not saved | __________ | __________ |
| UI-014 | Bill Generation | Click Generate Bill with no selected products | Validation message shown | __________ | __________ |
| UI-015 | SMS Summary | Generate final bill | SMS button appears only after successful bill generation | __________ | __________ |
| UI-016 | SMS Summary | Click Show SMS Summary | SMS text displayed in SMS section | __________ | __________ |
| UI-017 | Next Customer | Click Next Customer after bill generation | Customer form, OTP, cart, bill output, and SMS state reset | __________ | __________ |
| UI-018 | Dashboard | Generate a bill and refresh dashboard | Updated sales and item statistics shown | __________ | __________ |
| UI-019 | Responsive UI | Open in mobile view | Layout adjusts correctly to single-column mobile arrangement | __________ | __________ |
| UI-020 | Error Handling | Stop backend server and use UI | Friendly backend connection error message shown | __________ | __________ |

## Login Test Cases

| Test Case ID | Module | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|
| LGN-001 | Login | Correct username and password | Login successful | __________ | __________ |
| LGN-002 | Login | Wrong password | Invalid credentials message | __________ | __________ |
| LGN-003 | Login | Username shorter than 3 chars | Frontend/backend validation message | __________ | __________ |
| LGN-004 | Login | Password shorter than 4 chars | Frontend/backend validation message | __________ | __________ |
| LGN-005 | Login | Blank fields | Required field validation message | __________ | __________ |

## OTP Test Cases

| Test Case ID | Module | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|
| OTP-001 | OTP Generate | Valid 10-digit phone number | OTP generated successfully | __________ | __________ |
| OTP-002 | OTP Generate | Invalid phone number | Validation error | __________ | __________ |
| OTP-003 | OTP Verify | Correct OTP | OTP verified successfully | __________ | __________ |
| OTP-004 | OTP Verify | Incorrect OTP | Invalid OTP message | __________ | __________ |
| OTP-005 | OTP Verify | Empty OTP | Validation error | __________ | __________ |
| OTP-006 | OTP Verify | OTP with letters | Validation error for 4-digit OTP | __________ | __________ |
| OTP-007 | OTP Verify | Verify old OTP after regeneration | Verification fails | __________ | __________ |

## Product Selection Test Cases

| Test Case ID | Module | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|
| PRD-001 | Product Selection | Select quantity `1` for one product | Product appears in cart summary | __________ | __________ |
| PRD-002 | Product Selection | Select multiple products with different quantities | All selected items appear in cart summary with correct totals | __________ | __________ |
| PRD-003 | Product Selection | Change quantity from `1` to `3` | Cart summary updates to new quantity and total | __________ | __________ |
| PRD-004 | Product Selection | Change quantity from `2` to `0` | Product removed from cart summary | __________ | __________ |
| PRD-005 | Product Selection | Enter decimal quantity | Quantity should resolve to valid integer behavior only | __________ | __________ |
| PRD-006 | Product Selection | Enter negative quantity | Invalid quantity should not be accepted | __________ | __________ |

## Bill Generation Test Cases

| Test Case ID | Module | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|
| BILL-001 | Bill Generation | Valid customer, verified OTP, valid items | Bill generated and saved successfully | __________ | __________ |
| BILL-002 | Bill Generation | Recyclable item in cart | Next visit discount saved as `10%` | __________ | __________ |
| BILL-003 | Bill Generation | Reusable item in cart only | Next visit discount saved as `5%` | __________ | __________ |
| BILL-004 | Bill Generation | Eco-disposal item in cart only | Next visit discount saved as `3%` | __________ | __________ |
| BILL-005 | Bill Generation | Multiple categories in cart | Highest applicable next visit discount reflected | __________ | __________ |
| BILL-006 | Bill Generation | No selected products | Bill not generated, validation message shown | __________ | __________ |
| BILL-007 | Bill Generation | Unverified OTP | Bill not generated | __________ | __________ |
| BILL-008 | Bill Generation | Invalid product ID via API | Product not found error response | __________ | __________ |
| BILL-009 | Bill Generation | Final bill generated | SMS summary included in response | __________ | __________ |

## Dashboard Test Cases

| Test Case ID | Module | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|
| DSH-001 | Dashboard | No bills in system | All dashboard values are zero | __________ | __________ |
| DSH-002 | Dashboard | One recyclable-item bill saved | Total bills = 1, recyclable item count updated, total sales updated | __________ | __________ |
| DSH-003 | Dashboard | One reusable-item bill saved | Reusable item count updated | __________ | __________ |
| DSH-004 | Dashboard | One eco-disposal-item bill saved | Eco-disposal item count updated | __________ | __________ |
| DSH-005 | Dashboard | Multiple bills saved | Total sales and total bill count reflect all saved bills | __________ | __________ |
| DSH-006 | Dashboard | Refresh dashboard from UI | Latest values shown without page layout issues | __________ | __________ |

## Edge Case Test Cases

| Test Case ID | Module | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|
| EDGE-001 | Customer Validation | Name contains digits, such as `Raj123` | Validation error shown | __________ | __________ |
| EDGE-002 | Customer Validation | Phone contains spaces or letters | Validation error shown | __________ | __________ |
| EDGE-003 | OTP | Verify OTP after it has already been verified once | Verification fails because OTP was removed from memory | __________ | __________ |
| EDGE-004 | Bill Save | Same customer phone used again for next bill | Existing customer reused or updated successfully | __________ | __________ |
| EDGE-005 | Product Grid | Backend unavailable | UI shows connection error message | __________ | __________ |
| EDGE-006 | Dashboard | Large number of bills in database | Dashboard still loads and totals are correct | __________ | __________ |
| EDGE-007 | Bill Save | Customer email format invalid | Validation error shown | __________ | __________ |
| EDGE-008 | Quantity | Very large quantity entered | System should either process correctly or reject based on validation policy | __________ | __________ |
| EDGE-009 | Login | SQL/database unavailable during login | Friendly error or failure response shown | __________ | __________ |
| EDGE-010 | Full Flow | Login -> OTP -> Select products -> Generate bill -> Show SMS -> Refresh dashboard | Full workflow completes successfully without UI or API errors | __________ | __________ |

## Suggested Execution Order

1. Test login module
2. Test OTP generation and verification
3. Test product listing and product selection
4. Test final bill generation and SMS summary
5. Test dashboard updates
6. Test edge cases and invalid inputs

## Notes

- Fill `Actual Output` after executing each test.
- Mark `Status` as `Pass` or `Fail`.
- For API tests, use Postman and capture JSON responses.
- For UI tests, use browser screenshots if needed for documentation.
