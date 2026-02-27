# 🏦 E-Bank 

A secure, stateless banking backend system built with **Spring Boot** and **Java**. This API manages core financial operations, ensuring high data integrity and real-time user communication.

## 🚀 Features

- **Financial Operations:** Support for peer-to-peer transfers, deposits, and withdrawals.
- **Secure Authentication:** Stateless security implementation using **JWT (JSON Web Tokens)**.
- **Automated Notifications:** Real-time email alerts (Debit/Credit) using **Thymeleaf** templates and **Java Mail Sender**.
- **Performance Optimized:** Server-side pagination and sorting for transaction history via **Spring Data JPA**.
- **Global Error Handling:** Centralized exception management for consistent API responses.

## 🛠️ Tech Stack

- **Framework:** Spring Boot
- **Language:** Java
- **Security:** Spring Security, JWT
- **Database:** MySQL
- **Mailing:** Thymeleaf, Java Mail Sender
- **Utilities:** Lombok, ModelMapper, Jakarta Validation

## 🏗️ Architecture

The project follows a layered architecture to ensure separation of concerns:
1. **Controller Layer:** Handles REST requests and input validation.
2. **Service Layer:** Contains business logic and transaction management.
3. **Repository Layer:** Manages data persistence using Spring Data JPA.
4. **DTO Layer:** Decouples the API from the database entities for better security.


## 📋 API Endpoints

### 🔐 Authentication & Identity
| Method | Endpoint | Access | Description |
|:--- |:--- |:--- |:--- |
| `POST` | `/api/auth/register` | Public | Create a new customer account. |
| `POST` | `/api/auth/login` | Public | Authenticate and receive a JWT. |
| `POST` | `/api/auth/forget-pass` | Public | Request a password reset code via email. |
| `POST` | `/api/auth/reset-pass` | Public | Reset password using the received code. |
| `GET` | `/api/users/profile` | User | View current logged-in user profile. |
| `PUT` | `/api/users/update-pass` | User | Update password while logged in. |
| `PUT` | `/api/users/profile-picture` | User | Upload/Update profile image (MultipartFile). |
| `GET` | `/api/users` | **Admin** | View all users (Paginated). |

### 💳 Account Management
| Method | Endpoint | Access | Description |
|:--- |:--- |:--- |:--- |
| `GET` | `/api/accounts/me` | User | Get list of all accounts owned by the user. |
| `DELETE`| `/api/accounts/close/{accNum}` | User | Close a specific bank account. |

### 💸 Financial Transactions
| Method | Endpoint | Access | Description |
|:--- |:--- |:--- |:--- |
| `POST` | `/api/transactions/create` | User | Execute Deposit, Withdrawal, or Transfer. |
| `GET` | `/api/transactions/{accNum}` | Owner/Staff| View paged history for a specific account. |
| `GET` | `/api/transactions/all` | **Admin/Auditor**| View all system-wide transactions (Paginated). |

### 🛠️ Role Administration
| Method | Endpoint | Access | Description |
|:--- |:--- |:--- |:--- |
| `POST` | `/api/roles` | **Admin** | Create a new system role. |
| `PUT` | `/api/roles` | **Admin** | Update existing role details. |
| `GET` | `/api/roles` | **Admin** | List all available roles. |
| `DELETE`| `/api/roles/{id}` | **Admin** | Remove a role from the system. |

## ⚙️ Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/ahmounir25/E-Bank.git](https://github.com/ahmounir25/E-Bank.git)
