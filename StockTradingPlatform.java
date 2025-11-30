import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Stock class
class Stock {
    private String symbol;
    private String name;
    private double currentPrice;
    
    public Stock(String symbol, String name, double currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
    }
    
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double price) { this.currentPrice = price; }
    
    @Override
    public String toString() {
        return String.format("%s (%s): $%.2f", name, symbol, currentPrice);
    }
}

// Transaction class
class Transaction {
    private String type;
    private String symbol;
    private int quantity;
    private double price;
    private LocalDateTime timestamp;
    
    public Transaction(String type, String symbol, int quantity, double price) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s | %s %d shares of %s @ $%.2f | Total: $%.2f", 
            timestamp.format(formatter), type, quantity, symbol, price, quantity * price);
    }
}

// Portfolio class
class Portfolio {
    private Map<String, Integer> holdings;
    private List<Transaction> transactions;
    private double cash;
    
    public Portfolio(double initialCash) {
        this.holdings = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.cash = initialCash;
    }
    
    public void setCash(double cash) {
        this.cash = cash;
    }
    
    public boolean buyStock(String symbol, int quantity, double price) {
        double totalCost = quantity * price;
        if (totalCost > cash) {
            System.out.println("Insufficient funds! Available: $" + String.format("%.2f", cash));
            return false;
        }
        
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + quantity);
        cash -= totalCost;
        transactions.add(new Transaction("BUY", symbol, quantity, price));
        System.out.println("Successfully bought " + quantity + " shares of " + symbol);
        return true;
    }
    
    public boolean sellStock(String symbol, int quantity, double price) {
        if (!holdings.containsKey(symbol) || holdings.get(symbol) < quantity) {
            System.out.println("Insufficient shares! You own: " + holdings.getOrDefault(symbol, 0));
            return false;
        }
        
        holdings.put(symbol, holdings.get(symbol) - quantity);
        if (holdings.get(symbol) == 0) {
            holdings.remove(symbol);
        }
        cash += quantity * price;
        transactions.add(new Transaction("SELL", symbol, quantity, price));
        System.out.println("Successfully sold " + quantity + " shares of " + symbol);
        return true;
    }
    
    public void displayPortfolio(Map<String, Stock> stockMarket) {
        System.out.println("\n=== PORTFOLIO ===");
        System.out.println("Cash Balance: $" + String.format("%.2f", cash));
        System.out.println("\nHoldings:");
        
        double totalValue = cash;
        if (holdings.isEmpty()) {
            System.out.println("No stocks owned");
        } else {
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                String symbol = entry.getKey();
                int quantity = entry.getValue();
                Stock stock = stockMarket.get(symbol);
                if (stock != null) {
                    double currentPrice = stock.getCurrentPrice();
                    double value = quantity * currentPrice;
                    totalValue += value;
                    System.out.printf("%s: %d shares @ $%.2f = $%.2f%n", 
                        symbol, quantity, currentPrice, value);
                }
            }
        }
        System.out.println("\nTotal Portfolio Value: $" + String.format("%.2f", totalValue));
    }
    
    public void displayTransactionHistory() {
        System.out.println("\n=== TRANSACTION HISTORY ===");
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet");
        } else {
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println((i + 1) + ". " + transactions.get(i));
            }
        }
    }
    
    public double getCash() { return cash; }
    public Map<String, Integer> getHoldings() { return holdings; }
    public List<Transaction> getTransactions() { return transactions; }
}

// User class
class User {
    private String username;
    private Portfolio portfolio;
    
    public User(String username, double initialCash) {
        this.username = username;
        this.portfolio = new Portfolio(initialCash);
    }
    
    public String getUsername() { return username; }
    public Portfolio getPortfolio() { return portfolio; }
}

// Market class
class Market {
    private Map<String, Stock> stocks;
    private Random random;
    
    public Market() {
        stocks = new HashMap<>();
        random = new Random();
        initializeStocks();
    }
    
    private void initializeStocks() {
        stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 175.50));
        stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 140.25));
        stocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 380.75));
        stocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 155.30));
        stocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 245.60));
    }
    
    public void displayMarket() {
        System.out.println("\n=== MARKET DATA ===");
        for (Stock stock : stocks.values()) {
            System.out.println(stock);
        }
    }
    
    public void updatePrices() {
        System.out.println("\n=== UPDATING MARKET PRICES ===");
        for (Stock stock : stocks.values()) {
            double oldPrice = stock.getCurrentPrice();
            double changePercent = (random.nextDouble() - 0.5) * 0.1; // +/- 5%
            double newPrice = Math.max(1.0, oldPrice * (1 + changePercent));
            stock.setCurrentPrice(newPrice);
            
            String direction = newPrice > oldPrice ? "↑" : "↓";
            double change = newPrice - oldPrice;
            System.out.printf("%s: $%.2f → $%.2f %s (%.2f%%)%n", 
                stock.getSymbol(), oldPrice, newPrice, direction, 
                (change / oldPrice) * 100);
        }
        System.out.println("Market prices updated!");
    }
    
    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }
    
    public Map<String, Stock> getAllStocks() {
        return stocks;
    }
}

// File persistence class
class DataPersistence {
    private static final String FILE_NAME = "portfolio_data.txt";
    
    public static void savePortfolio(User user) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println(user.getUsername());
            writer.println(user.getPortfolio().getCash());
            
            // Save holdings
            writer.println("HOLDINGS");
            for (Map.Entry<String, Integer> entry : user.getPortfolio().getHoldings().entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
            
            // Save transactions
            writer.println("TRANSACTIONS");
            for (Transaction t : user.getPortfolio().getTransactions()) {
                writer.println(t.getType() + "," + t.getSymbol() + "," + 
                    t.getQuantity() + "," + t.getPrice() + "," + t.getTimestamp());
            }
            
            System.out.println("\n✓ Portfolio saved successfully to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("✗ Error saving portfolio: " + e.getMessage());
        }
    }
    
    public static User loadPortfolio(String username) {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String savedUsername = reader.readLine();
            if (!savedUsername.equals(username)) {
                return null;
            }
            
            double cash = Double.parseDouble(reader.readLine());
            User user = new User(username, 0);
            user.getPortfolio().getHoldings().clear();
            user.getPortfolio().getTransactions().clear();
            
            String line;
            boolean readingHoldings = false;
            boolean readingTransactions = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("HOLDINGS")) {
                    readingHoldings = true;
                    readingTransactions = false;
                    continue;
                } else if (line.equals("TRANSACTIONS")) {
                    readingHoldings = false;
                    readingTransactions = true;
                    continue;
                }
                
                if (readingHoldings && !line.isEmpty()) {
                    String[] parts = line.split(",");
                    user.getPortfolio().getHoldings().put(parts[0], Integer.parseInt(parts[1]));
                } else if (readingTransactions && !line.isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        Transaction t = new Transaction(parts[0], parts[1], 
                            Integer.parseInt(parts[2]), Double.parseDouble(parts[3]));
                        user.getPortfolio().getTransactions().add(t);
                    }
                }
            }
            
            // Set the cash after loading everything
            user.getPortfolio().setCash(cash);
            
            System.out.println("✓ Portfolio loaded successfully!");
            return user;
        } catch (IOException | NumberFormatException e) {
            System.out.println("✗ Error loading portfolio: " + e.getMessage());
            return null;
        }
    }
}

// Main trading platform
public class StockTradingPlatform {
    private User currentUser;
    private Market market;
    private Scanner scanner;
    
    public StockTradingPlatform() {
        market = new Market();
        scanner = new Scanner(System.in);
    }
    
    public void start() {
        System.out.println("=================================");
        System.out.println("  STOCK TRADING PLATFORM");
        System.out.println("=================================");
        
        login();
        mainMenu();
    }
    
    private void login() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Load existing portfolio? (y/n): ");
        String choice = scanner.nextLine().trim();
        
        if (choice.equalsIgnoreCase("y")) {
            currentUser = DataPersistence.loadPortfolio(username);
            if (currentUser == null) {
                System.out.println("No saved portfolio found. Creating new account.");
                System.out.print("Enter initial cash amount: $");
                double initialCash = getValidDouble();
                currentUser = new User(username, initialCash);
            }
        } else {
            System.out.print("Enter initial cash amount: $");
            double initialCash = getValidDouble();
            currentUser = new User(username, initialCash);
        }
        
        System.out.println("\n✓ Welcome, " + currentUser.getUsername() + "!");
    }
    
    private void mainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. View Transaction History");
            System.out.println("6. Update Market Prices");
            System.out.println("7. Save Portfolio");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");
            
            int choice = getValidInt();
            
            switch (choice) {
                case 1:
                    market.displayMarket();
                    break;
                case 2:
                    buyStock();
                    break;
                case 3:
                    sellStock();
                    break;
                case 4:
                    currentUser.getPortfolio().displayPortfolio(market.getAllStocks());
                    break;
                case 5:
                    currentUser.getPortfolio().displayTransactionHistory();
                    break;
                case 6:
                    market.updatePrices();
                    break;
                case 7:
                    DataPersistence.savePortfolio(currentUser);
                    break;
                case 8:
                    System.out.print("\nSave before exit? (y/n): ");
                    String saveChoice = scanner.nextLine().trim();
                    if (saveChoice.equalsIgnoreCase("y")) {
                        DataPersistence.savePortfolio(currentUser);
                    }
                    System.out.println("\nThank you for using Stock Trading Platform!");
                    System.out.println("Goodbye, " + currentUser.getUsername() + "!");
                    scanner.close();
                    return;
                default:
                    System.out.println("✗ Invalid option! Please choose 1-8.");
            }
        }
    }
    
    private void buyStock() {
        market.displayMarket();
        System.out.print("\nEnter stock symbol to buy: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        Stock stock = market.getStock(symbol);
        if (stock == null) {
            System.out.println("✗ Stock not found! Available symbols: AAPL, GOOGL, MSFT, AMZN, TSLA");
            return;
        }
        
        System.out.println("Current price of " + symbol + ": $" + String.format("%.2f", stock.getCurrentPrice()));
        System.out.print("Enter quantity to buy: ");
        int quantity = getValidInt();
        
        if (quantity <= 0) {
            System.out.println("✗ Quantity must be positive!");
            return;
        }
        
        double totalCost = quantity * stock.getCurrentPrice();
        System.out.printf("\nTotal cost: $%.2f%n", totalCost);
        System.out.print("Confirm purchase? (y/n): ");
        String confirm = scanner.nextLine().trim();
        
        if (confirm.equalsIgnoreCase("y")) {
            currentUser.getPortfolio().buyStock(symbol, quantity, stock.getCurrentPrice());
        } else {
            System.out.println("Purchase cancelled.");
        }
    }
    
    private void sellStock() {
        Map<String, Integer> holdings = currentUser.getPortfolio().getHoldings();
        
        if (holdings.isEmpty()) {
            System.out.println("✗ You don't own any stocks to sell!");
            return;
        }
        
        System.out.println("\n=== YOUR HOLDINGS ===");
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = market.getStock(entry.getKey());
            if (stock != null) {
                System.out.printf("%s: %d shares @ $%.2f%n", 
                    entry.getKey(), entry.getValue(), stock.getCurrentPrice());
            }
        }
        
        System.out.print("\nEnter stock symbol to sell: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        Stock stock = market.getStock(symbol);
        if (stock == null) {
            System.out.println("✗ Stock not found!");
            return;
        }
        
        if (!holdings.containsKey(symbol)) {
            System.out.println("✗ You don't own any shares of " + symbol + "!");
            return;
        }
        
        System.out.println("You own " + holdings.get(symbol) + " shares of " + symbol);
        System.out.println("Current price: $" + String.format("%.2f", stock.getCurrentPrice()));
        System.out.print("Enter quantity to sell: ");
        int quantity = getValidInt();
        
        if (quantity <= 0) {
            System.out.println("✗ Quantity must be positive!");
            return;
        }
        
        double totalValue = quantity * stock.getCurrentPrice();
        System.out.printf("\nTotal value: $%.2f%n", totalValue);
        System.out.print("Confirm sale? (y/n): ");
        String confirm = scanner.nextLine().trim();
        
        if (confirm.equalsIgnoreCase("y")) {
            currentUser.getPortfolio().sellStock(symbol, quantity, stock.getCurrentPrice());
        } else {
            System.out.println("Sale cancelled.");
        }
    }
    
    private int getValidInt() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a valid number: ");
            }
        }
    }
    
    private double getValidDouble() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a valid number: ");
            }
        }
    }
    
    public static void main(String[] args) {
        StockTradingPlatform platform = new StockTradingPlatform();
        platform.start();
    }
}
