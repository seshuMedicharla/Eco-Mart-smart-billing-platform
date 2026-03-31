# EcoWaste Smart Billing System

EcoWaste Smart Billing System is a full-stack student project built to promote sustainable shopping and responsible consumption. The system demonstrates how billing software can support eco-friendly purchasing by classifying products into waste categories, generating bills, assigning next-visit discounts, and presenting simple sales analytics.

## SDG Mapping

This project is aligned with **United Nations Sustainable Development Goal 12: Responsible Consumption and Production**.

How the project supports SDG 12:

- Encourages users to choose recyclable and reusable products
- Promotes sustainable waste awareness through category-based billing
- Rewards eco-friendly product selection with next-visit discount logic
- Tracks sales and waste-category movement through a dashboard

## Features

- Admin login with validation
- Customer details capture
- OTP simulation for local/demo verification
- Product catalog with image, price, and waste category
- Product quantity selection with live cart summary
- Final bill generation and save flow
- Next-visit discount calculation
- Waste categories:
  - Recyclable
  - Reusable
  - Eco-Disposal
- SMS summary simulation after bill generation
- Dashboard for total sales and category-wise item counts
- Frontend and backend validation

## Technology Stack

- Frontend:
  - HTML
  - CSS
  - JavaScript
- Backend:
  - Java 17
  - Spring Boot
  - Spring Web
  - Spring Data JPA
- Database:
  - MySQL
- Build Tool:
  - Maven
- Utilities:
  - Lombok

## Project Structure

```text
ProjectFullstack/
├── src/main/java/com/ecowaste/smartbilling
│   ├── config
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
├── src/main/resources
│   ├── static
│   │   ├── index.html
│   │   ├── style.css
│   │   └── script.js
│   ├── application.properties
│   └── schema.sql
├── README.md
└── TESTING_GUIDE.md
```

## Setup Steps

### 1. Create the database

```sql
CREATE DATABASE eco_waste_billing;
```

### 2. Configure database credentials

Update environment variables if needed:

```bash
export DB_URL=jdbc:mysql://localhost:3306/eco_waste_billing
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
```

You can also directly update [application.properties](/Users/apple/Downloads/ProjectFullstack/src/main/resources/application.properties) if preferred.

### 3. Run the project

```bash
mvn spring-boot:run
```

### 4. Open the application

Open this URL in a browser:

```text
http://localhost:8080
```

## Default Admin Login

- Username: `admin`
- Password: `admin123`

## Core Modules

- Admin Module
  - Login API and validation
- Customer Module
  - Customer details and OTP simulation
- Product Module
  - Product fetch from MySQL
- Billing Module
  - Bill calculation, bill save, item save, SMS summary
- Dashboard Module
  - Total sales and waste-category statistics

## API Overview

- `POST /api/auth/login`
- `POST /api/customers/otp/generate`
- `POST /api/customers/otp/verify`
- `GET /api/products`
- `POST /api/bills/save`
- `GET /api/dashboard/stats`

## Screenshots

Add screenshots here before final submission:

- Screenshot 1: Home page / Admin login
- Screenshot 2: Product listing and customer details
- Screenshot 3: OTP verification
- Screenshot 4: Final bill generation
- Screenshot 5: SMS summary simulation
- Screenshot 6: Dashboard statistics

## Testing

Manual testing cases are available in:

- [TESTING_GUIDE.md](/Users/apple/Downloads/ProjectFullstack/TESTING_GUIDE.md)

## Submission Notes

This project is designed to be:

- Simple enough for academic presentation
- Clean enough for code review
- Practical enough to demonstrate full-stack integration
- Relevant to sustainability and SDG-focused innovation
