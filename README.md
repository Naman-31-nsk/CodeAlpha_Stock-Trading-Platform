# CodeAlpha_Stock-Trading-Platform
📈 Stock Trading Platform
A console-based stock trading simulation platform built in Java that allows users to buy and sell stocks, track their portfolio performance, and persist their data across sessions.

📋 Table of Contents

Features
Technologies Used
Installation
How to Run
Usage Guide
Program Structure
File Persistence
Screenshots
Future Enhancements
Contributing
License
Contact

✨ Features
Core Functionality

📊 Real-time Market Data Display: View current stock prices for major companies
💰 Buy/Sell Operations: Execute stock transactions with validation
📁 Portfolio Management: Track holdings, cash balance, and total portfolio value
📜 Transaction History: Complete record of all buy/sell operations with timestamps
🔄 Dynamic Price Updates: Simulate market fluctuations with random price changes
💾 Data Persistence: Save and load portfolio data using file I/O
✅ Input Validation: Robust error handling for user inputs
🔐 User Authentication: Username-based account system

Available Stocks
SymbolCompany NameInitial Price
AAPLApple Inc.$175.50
GOOGLAlphabet Inc.$140.25
MSFTMicrosoft Corp.$380.75
AMZNAmazon.com Inc.$155.30
TSLATesla Inc.$245.60
🛠 Technologies Used

Language: Java (JDK 8 or higher)
Concepts: Object-Oriented Programming (OOP)
Data Structures: HashMap, ArrayList
File I/O: BufferedReader, PrintWriter
Date/Time: LocalDateTime, DateTimeFormatter

📖 Usage Guide
First Time Setup

Start the program

   Enter username: JohnDoe
   Load existing portfolio? (y/n): n
   Enter initial cash amount: $10000

Main Menu

   === MAIN MENU ===
   1. View Market Data
   2. Buy Stock
   3. Sell Stock
   4. View Portfolio
   5. View Transaction History
   6. Update Market Prices
   7. Save Portfolio
   8. Exit
